package com.frizzer.fileapi.service

import com.frizzer.contractapi.dto.SongDTO
import com.frizzer.contractapi.dto.SongMetadataDTO
import com.frizzer.contractapi.entity.Song
import com.frizzer.contractapi.entity.StorageType
import com.frizzer.fileapi.repository.SongRepository
import io.awspring.cloud.s3.S3Template
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.mp3.Mp3Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.xml.sax.helpers.DefaultHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime

@Service
class FileService(
    private val sqsService: SqsService,
    private val songRepository: SongRepository,
    private val s3SongRepository: S3Template
) {

    private val log: Logger = LoggerFactory.getLogger(FileService::class.java)

    @Value("\${file-api.base-path}")
    private val basePath = ""

    @Value("\${file-api.bucket-name}")
    private val bucketName = ""

    fun upload(username: String, filename: String): Flux<DataBuffer> {
        return songRepository.findFirstByUsernameAndFilename(username, filename)
            .flatMap {
                when (it.storageType) {
                    StorageType.S3 -> uploadFromS3(it.username, it.filename, StorageType.S3)
                    StorageType.LOCAL -> uploadFromLocal(it.username, it.filename, StorageType.LOCAL)
                }
            }
    }

    fun uploadMetadata(username: String, filename: String): Mono<SongMetadataDTO> {
        val metadata = Metadata()
        val songMetadata = SongMetadataDTO("", "", "", "", "", "")
        consumeDataBuffer(upload(username, filename)).use { inputStream ->
            Mp3Parser().parse(inputStream, DefaultHandler(), metadata, ParseContext())
            songMetadata.author = metadata.get("xmpDM:artist") ?: ""
            songMetadata.name = metadata.get("dc:title") ?: ""
            songMetadata.album = metadata.get("xmpDM:album") ?: ""
        }
        return songMetadata.toMono()
    }

    fun uploadFromS3(username: String, filename: String, type: StorageType): Flux<DataBuffer> {
        val bytes = s3SongRepository.download(bucketName, findPath(username, filename, type)).contentAsByteArray
        val buffer = DefaultDataBufferFactory().allocateBuffer(bytes.size).write(bytes)
        return Flux.just(buffer)
    }

    fun uploadFromLocal(username: String, filename: String, type: StorageType) =
        DataBufferUtils.read(Path.of(findPath(username, filename, type)), DefaultDataBufferFactory(), 4096)

    fun save(username: String, songs: Flux<FilePart>): Flux<Song> {
        var source = StorageType.S3
        return writeToS3(username, songs)
            .onErrorResume {
                source = StorageType.LOCAL
                log.error(it.message)
                writeToLocal(username, songs)
            }
            .map {
                Song(
                    username = username,
                    filename = it.filename(),
                    storageType = source,
                    filePath = findPath(username, it, source),
                    time = OffsetDateTime.now(),
                    id = null
                )
            }.flatMap {
                sqsService.sendSqsMessage(SongDTO(it.username, it.filename))
                songRepository.save(it)
            }
    }

    fun findPath(username: String, song: FilePart, type: StorageType): String {
        return findPath(username, song.filename(), type)
    }

    fun findPath(username: String, filename: String, type: StorageType): String {
        return when (type) {
            StorageType.S3 -> "$username-${filename}"
            StorageType.LOCAL -> Paths.get("$basePath\\$username-${filename}").toAbsolutePath().toString()
        }
    }

    fun writeToS3(username: String, songs: Flux<FilePart>): Flux<FilePart> {
        return songs.map { file ->
            val outputStream = consumeDataBuffer(file.content())
            s3SongRepository.upload(bucketName, "$username-${file.filename()}", outputStream)
            file
        }
    }

    fun consumeDataBuffer(data: Flux<DataBuffer>): PipedInputStream {
        val osPipe = PipedOutputStream()
        val isPipe = PipedInputStream(osPipe)

        DataBufferUtils.write(data, osPipe)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnComplete { osPipe.close() }
            .subscribe { DataBufferUtils.releaseConsumer() }
        return isPipe
    }

    fun writeToLocal(username: String, songs: Flux<FilePart>): Flux<FilePart> {
        return songs.flatMap {
            val filePath = Paths.get("$basePath\\$username-${it.filename()}")
            val bufferedData = DataBufferUtils.join(it.content())
            DataBufferUtils.write(bufferedData, filePath, StandardOpenOption.CREATE)
                .thenReturn(it)
        }

    }

}