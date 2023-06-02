package com.frizzer.contractapi.entity

import java.time.OffsetDateTime
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "song")
data class Song (
    val username : String,
    val filename : String,
    val storageType : StorageType,
    val filePath : String,
    val time : OffsetDateTime,
    @Id
    val id : Int?
)