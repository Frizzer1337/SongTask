package com.frizzer.enricherapi.service

import com.frizzer.contractapi.dto.SongDTO
import com.frizzer.contractapi.dto.SongMetadataDTO
import com.frizzer.enricherapi.client.FileClient
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class EnricherService(
    private val sqsService: SqsService,
    private val fileClient: FileClient
) {

    @Value("\${enricher-api.token}")
    val accessToken = ""

    private val client: WebClient = WebClient.create("https://api.spotify.com/v1/")

    @SqsListener("newSong")
    fun consumeSqsMessage(song: SongDTO) =
        fileClient.loadSongMetadata(song.username, song.filename)
            .flatMap {
                fetchMetadataFromSpotify(it).map { json ->
                    it.authorLink = com.jayway.jsonpath.JsonPath.parse(json)
                        .read("$.tracks.items[0].album.artists[0].external_urls.spotify")
                    it.trackLink = com.jayway.jsonpath.JsonPath.parse(json)
                        .read("$.tracks.items[0].external_urls.spotify")
                    it.albumLink = com.jayway.jsonpath.JsonPath.parse(json)
                        .read("$.tracks.items[0].album.external_urls.spotify")
                    sqsService.sendSqsMessage(it)
                }
            }
            .subscribe()


    fun fetchMetadataFromSpotify(metadata: SongMetadataDTO): Mono<String> =
        client
            .get()
            .uri { builder ->
                builder.path("/search/")
                    .queryParam("q", "remaster%20track:${metadata.name}%20artist:${metadata.author}")
                    .queryParam("type", "track")
                    .queryParam("limit", "1")
                    .build()
            }
            .headers { it.setBearerAuth(accessToken) }
            .retrieve()
            .bodyToMono(String::class.java)
}