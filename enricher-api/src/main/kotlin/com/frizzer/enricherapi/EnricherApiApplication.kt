package com.frizzer.enricherapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableReactiveFeignClients
class EnricherApiApplication

fun main(args: Array<String>) {
    runApplication<EnricherApiApplication>(*args)
}
