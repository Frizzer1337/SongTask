package com.frizzer.songapi.service

import com.frizzer.contractapi.dto.SongMetadataDTO
import com.frizzer.contractapi.dto.toSongMetadata
import com.frizzer.songapi.repository.SongRepository
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Service

@Service
class SongService(val songRepository: SongRepository) {

    @SqsListener("newMetadata")
    fun metadataListener(metadata: SongMetadataDTO) = songRepository.save(metadata.toSongMetadata()).subscribe()

    fun findAllMetadata() = songRepository.findAll()

    fun findMetadataByName(name : String) = songRepository.findByName(name)

}