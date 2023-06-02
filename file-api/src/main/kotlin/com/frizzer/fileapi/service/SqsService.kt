package com.frizzer.fileapi.service

import com.frizzer.contractapi.dto.SongDTO
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.stereotype.Service

@Service
class SqsService(private val sqsTemplate: SqsTemplate) {

    fun sendSqsMessage(song : SongDTO) = sqsTemplate.send<SongDTO> {
        it.queue("newSong")
        it.payload(song)
    }.also { println("Message with ${song.filename} and ${song.username} was sent") }

}