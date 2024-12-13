package org.example.project.di

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.example.project.data.remote.ContentApi
import org.example.project.data.remote.ContentApiImpl

object NetworkModule {
    private const val TIMEOUT_MS = 5000L

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
            connectTimeoutMillis = TIMEOUT_MS
        }
    }

    val contentApi: ContentApi by lazy {
        ContentApiImpl(httpClient)
    }
} 