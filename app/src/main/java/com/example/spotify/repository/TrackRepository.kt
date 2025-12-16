package com.example.spotify.repository

import com.example.spotify.api.KtorClient
import com.example.spotify.model.Track
import com.example.spotify.model.TrackResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TrackRepository {

    suspend fun getsongs(): List<Track>{
        val response: TrackResponse =  KtorClient.client.get("https://api.jamendo.com/v3.0/tracks/"){
            parameter("client_id","ef4e030c")
            parameter("format","json")
            parameter("limit",20)
        }.body()
        return response.results
    }
}