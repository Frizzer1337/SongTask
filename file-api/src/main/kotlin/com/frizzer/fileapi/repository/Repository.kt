package com.frizzer.fileapi.repository

import com.frizzer.contractapi.entity.Song
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface SongRepository : R2dbcRepository<Song, String> {

    fun findFirstByUsernameAndFilename(username: String, filename: String): Flux<Song>
}