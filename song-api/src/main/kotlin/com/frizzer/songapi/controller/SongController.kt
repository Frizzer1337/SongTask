package com.frizzer.songapi.controller

import com.frizzer.contractapi.entity.SongMetadata
import com.frizzer.songapi.service.SongService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/song")
class SongController(val songService: SongService) {

    @GetMapping("/")
    fun findAll(): Flux<SongMetadata> = songService.findAllMetadata()

    @GetMapping("/{name}")
    fun findByName(@PathVariable name: String) = songService.findMetadataByName(name)
}