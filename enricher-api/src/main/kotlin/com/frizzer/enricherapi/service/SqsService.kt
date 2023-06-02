package com.frizzer.enricherapi.service

import com.frizzer.contractapi.dto.SongMetadataDTO
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.stereotype.Service

@Service
class SqsService(private val sqsTemplate: SqsTemplate) {

    fun sendSqsMessage(metadata : SongMetadataDTO) = sqsTemplate.send<SongMetadataDTO> {
        it.queue("newMetadata")
        it.payload(metadata)
    }.also { println("Metadata with ${metadata.name} and ${metadata.author} was sent") }

}