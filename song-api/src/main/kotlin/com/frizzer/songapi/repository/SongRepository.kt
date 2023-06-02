package com.frizzer.songapi.repository

import com.frizzer.contractapi.entity.SongMetadata
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface SongRepository : R2dbcRepository<SongMetadata, Int> {
    fun findByName(name: String): Mono<SongMetadata>
}