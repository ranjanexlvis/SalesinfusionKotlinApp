package com.example.kmmexlvistech.common

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual object CryptoProvider {
    actual fun getEngine(): CryptoEngine = AndroidCryptoEngine()
}

class AndroidCryptoEngine : CryptoEngine {

    private val CIPHER = "AES/CBC/PKCS5Padding"
    private val BLOCK_SIZE = 16
    private val KEY_SIZE_BYTES = 32 // AES-256 key size (OpenSSL pads/truncates)

    private fun normalizeAes256Key(secret: String): ByteArray {
        val keyBytes = secret.toByteArray(Charsets.UTF_8)
        val padded = ByteArray(KEY_SIZE_BYTES) { 0 }
        System.arraycopy(keyBytes, 0, padded, 0, minOf(keyBytes.size, KEY_SIZE_BYTES))
        return padded
    }

    override fun encrypt(data: ByteArray, secret: String): String {
        val keySpec = SecretKeySpec(normalizeAes256Key(secret), "AES")

        // Generate IV
        val ivBytes = ByteArray(BLOCK_SIZE)
        SecureRandom().nextBytes(ivBytes)

        // PHP logic: bin2hex(iv) then substr(16)
        val ivHex = ivBytes.toHexLower()
        val ivString = ivHex.substring(16)          // 16 chars (not bytes!)
        val ivFinal = ivString.toByteArray(Charsets.US_ASCII) // exactly 16 bytes

        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ivFinal))
        val encryptedRaw = cipher.doFinal(data)

        // IMPORTANT: PHP openssl_encrypt(..., options=0, ...) returns BASE64 STRING (not raw bytes).
        val encryptedB64 = Base64.encodeToString(encryptedRaw, Base64.NO_WRAP)

        // Output = base64_encode( iv(16 ASCII) + encryptedB64String )
        val combined = ivFinal + encryptedB64.toByteArray(Charsets.US_ASCII)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    override fun decrypt(data: ByteArray, secret: String): String {
        // `data` should be the ASCII bytes of the base64 string (payload)
        val outerB64 = data.toString(Charsets.US_ASCII)
        val decoded = Base64.decode(outerB64, Base64.NO_WRAP)

        // PHP IV = 16 characters, not 16 bytes of original IV
        val ivLength = 16
        val iv = decoded.copyOfRange(0, ivLength)
        val encryptedB64Bytes = decoded.copyOfRange(ivLength, decoded.size)
        val encryptedB64 = encryptedB64Bytes.toString(Charsets.US_ASCII)
        val cipherRaw = Base64.decode(encryptedB64, Base64.NO_WRAP)

        val keySpec = SecretKeySpec(normalizeAes256Key(secret), "AES")
        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))

        return String(cipher.doFinal(cipherRaw), Charsets.UTF_8)
    }

    private fun ByteArray.toHexLower(): String {
        val hexChars = "0123456789abcdef".toCharArray()
        val result = CharArray(this.size * 2)
        var j = 0
        for (b in this) {
            val v = b.toInt() and 0xFF
            result[j++] = hexChars[v ushr 4]
            result[j++] = hexChars[v and 0x0F]
        }
        return String(result)
    }
}

