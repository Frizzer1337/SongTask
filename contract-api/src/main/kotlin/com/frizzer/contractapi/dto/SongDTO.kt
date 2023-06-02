package com.frizzer.contractapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SongDTO (
    @JsonProperty("username")
    val username : String,
    @JsonProperty("filename")
    val filename : String
)