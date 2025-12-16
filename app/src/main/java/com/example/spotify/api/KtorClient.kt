package com.example.spotify.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

object KtorClient {

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
    }
}
