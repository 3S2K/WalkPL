package org.example.project.core

object Constants {
    const val SERVER_IP = "192.168.0.2"  // 개발 서버 IP
    const val SERVER_PORT = 8000         // 서버 포트
    const val BASE_URL = "http://$SERVER_IP:$SERVER_PORT"
    
    object Endpoints {
        const val CONTENTS = "/contents"
        const val SEARCH = "/contents/search"
        const val STREAM = "/stream"
    }
} 