package com.frizzer.enricherapi.client

import com.frizzer.contractapi.dto.SongMetadataDTO
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "file-api", url = "\${file-api.url}")
@Service
interface FileClient {
    @RequestMapping(method = [RequestMethod.GET], value = ["/{username}/{filename}/metadata"])
    fun loadSongMetadata(
        @PathVariable("username") username: String,
        @PathVariable("filename") filename: String
    ): Mono<SongMetadataDTO>
}