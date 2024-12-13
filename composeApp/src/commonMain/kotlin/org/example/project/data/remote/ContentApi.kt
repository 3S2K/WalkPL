package org.example.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.withTimeout
import org.example.project.domain.model.Track
import org.example.project.domain.model.ContentType as TrackContentType
import org.example.project.core.Constants
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.milliseconds

interface ContentApi {
    suspend fun getContents(type: TrackContentType? = null): List<Track>
    suspend fun getContent(id: String): Track
    suspend fun searchContents(query: String): List<Track>
    suspend fun downloadTrack(url: String, onProgress: (Float) -> Unit): ByteArray
    suspend fun checkConnection()
}

class ContentApiImpl(private val client: HttpClient) : ContentApi {
    override suspend fun getContents(type: TrackContentType?): List<Track> {
        return try {
            withTimeout(5000) { // 5초 타임아웃 설정
                val response = client.get("${Constants.BASE_URL}${Constants.Endpoints.CONTENTS}") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (!response.status.isSuccess()) {
                    println("서버 응답 실패: ${response.status}")
                    throw Exception("서버 응답 실패: ${response.status}")
                }
                response.body<List<Track>>()
            }
        } catch (e: Exception) {
            println("API 호출 실패: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getContent(id: String): Track {
        return try {
            val response = client.get("${Constants.BASE_URL}${Constants.Endpoints.CONTENTS}/$id")
            println("단일 트랙 응답: ${response.bodyAsText()}")
            response.body<Track>()
        } catch (e: Exception) {
            println("트랙 조회 실패: ${e.message}")
            throw e
        }
    }

    override suspend fun searchContents(query: String): List<Track> {
        return try {
            val response = client.get("${Constants.BASE_URL}${Constants.Endpoints.SEARCH}") {
                parameter("query", query)
            }
            println("검색 응답: ${response.bodyAsText()}")
            response.body<List<Track>>()
        } catch (e: Exception) {
            println("검색 실패: ${e.message}")
            emptyList()
        }
    }

    override suspend fun downloadTrack(url: String, onProgress: (Float) -> Unit): ByteArray {
        return try {
            val fullUrl = if (!url.startsWith("http")) "${Constants.BASE_URL}$url" else url
            println("다운로드 시도: $fullUrl")
            
            val response = client.get(fullUrl)
            val contentLength = response.contentLength() ?: 0L
            
            return response.bodyAsChannel().let { channel ->
                val buffer = ByteArray(contentLength.toInt())
                var offset = 0
                
                while (!channel.isClosedForRead) {
                    val bytes = channel.readAvailable(buffer, offset, buffer.size - offset)
                    if (bytes == -1) break
                    offset += bytes
                    if (contentLength > 0) {
                        onProgress(offset.toFloat() / contentLength.toFloat())
                    }
                }
                
                buffer
            }
        } catch (e: Exception) {
            println("다운로드 실패: ${e.message}")
            throw e
        }
    }

    override suspend fun checkConnection() {
        try {
            withTimeout(5000) {  // 5초 타임아웃
                val response = client.get("${Constants.BASE_URL}${Constants.Endpoints.CONTENTS}") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (!response.status.isSuccess()) {
                    throw Exception("서버 응답 실패: ${response.status}")
                }
            }
        } catch (e: Exception) {
            throw Exception("서버 연결 실패: ${e.message}")
        }
    }
} 