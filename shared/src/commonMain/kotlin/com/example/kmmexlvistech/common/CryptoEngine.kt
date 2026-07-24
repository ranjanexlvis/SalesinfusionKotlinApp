package com.example.kmmexlvistech.common

// commonMain
interface CryptoEngine {
    fun encrypt(data: ByteArray, secret: String): String
    fun decrypt(data: ByteArray, secret: String): String
}

expect object CryptoProvider {
    fun getEngine(): CryptoEngine
}
