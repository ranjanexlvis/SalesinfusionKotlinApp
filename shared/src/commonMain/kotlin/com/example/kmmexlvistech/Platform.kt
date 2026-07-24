package com.example.kmmexlvistech

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform