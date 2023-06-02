package com.frizzer.songapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SongApiApplication

fun main(args: Array<String>) {
    runApplication<SongApiApplication>(*args)
}
