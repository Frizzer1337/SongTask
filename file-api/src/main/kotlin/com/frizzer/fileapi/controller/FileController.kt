package com.frizzer.fileapi.controller

import com.frizzer.contractapi.dto.SongMetadataDTO
import com.frizzer.contractapi.entity.Song
import com.frizzer.fileapi.service.FileService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/files")
class FileController(val fileService: FileService) {

    @PostMapping("/")
    fun download(
        @RequestPart("username") username: String,
        @RequestPart("songs") songs: Flux<FilePart>
    ): Flux<Song> = fileService.save(username, songs)

    @GetMapping("/{username}/{filename}")
    fun upload(
        @PathVariable("username") username: String,
        @PathVariable("filename") filename: String
    ): Flux<DataBuffer> = fileService.upload(username, filename)

    @GetMapping("/{username}/{filename}/metadata")
    fun uploadMetadata(
        @PathVariable("username") username: String,
        @PathVariable("filename") filename: String
    ) : Mono<SongMetadataDTO> = fileService.uploadMetadata(username,filename)

}