package com.frizzer.contractapi.entity

import javax.persistence.Id
import javax.persistence.Table

@Table(name = "song_metadata")
data class SongMetadata(
    val name: String,
    val author: String,
    val album: String,
    val trackLink: String,
    val authorLink: String,
    val albumLink: String,
    @Id
    val id: Int?
)