package com.frizzer.contractapi.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.frizzer.contractapi.entity.SongMetadata

data class SongMetadataDTO(
    @JsonProperty("name")
    var name: String,
    @JsonProperty("author")
    var author: String,
    @JsonProperty("album")
    var album: String,
    @JsonProperty("trackLink")
    var trackLink: String,
    @JsonProperty("authorLink")
    var authorLink: String,
    @JsonProperty("albumLink")
    var albumLink: String
)

fun SongMetadataDTO.toSongMetadata() = SongMetadata(
    name = name,
    author = author,
    album = album,
    trackLink = trackLink,
    authorLink = authorLink,
    albumLink = albumLink,
    id = null
)