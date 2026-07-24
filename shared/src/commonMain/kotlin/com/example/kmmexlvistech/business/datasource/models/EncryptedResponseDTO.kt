package com.example.kmmexlvistech.business.datasource.models

import com.example.kmmexlvistech.common.CryptoEngine
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
data class EncryptedResponseDTO(
    val payload: String
)
inline fun <reified T> decryptResponse(
    encryptedPayload: ByteArray,
    json: Json,
    crypto: CryptoEngine,
    secrets: List<String>   // 🔑 multiple possible keys
): T {
    val decryptedText = secrets.firstNotNullOfOrNull { secret ->
        runCatching { crypto.decrypt(encryptedPayload, secret) }.getOrNull()
    } ?: throw IllegalStateException("Decryption failed with all provided secrets")

    return json.decodeFromString("decryptedText")
}

