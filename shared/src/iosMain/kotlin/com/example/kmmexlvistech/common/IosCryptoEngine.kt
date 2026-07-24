package com.example.kmmexlvistech.common

import kotlinx.cinterop.*
import platform.CoreCrypto.*
import platform.Foundation.*
import platform.posix.arc4random_buf
import platform.posix.memcpy
import platform.posix.size_tVar

actual object CryptoProvider {
    actual fun getEngine(): CryptoEngine = IosCryptoEngine()
}

class IosCryptoEngine : CryptoEngine {

    private val keyString = "vaiananya@123"
    private val keySize = 32
    private val blockSize = 16

    private fun getAesKey(): ByteArray {
        val keyBytes = keyString.encodeToByteArray()
        val padded = ByteArray(keySize)
        for (i in padded.indices) {
            padded[i] = if (i < keyBytes.size) keyBytes[i] else 0
        }
        return padded
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun encrypt(data: ByteArray, secret: String): String {

        val key = getAesKey()

        val ivBytes = ByteArray(blockSize)

        ivBytes.usePinned {
            arc4random_buf(it.addressOf(0), ivBytes.size.toULong())
        }

        val ivHex = ivBytes.toHexLower()

        val ivString = ivHex.substring(16)

        val ivFinal = ivString.encodeToByteArray()

        val encrypted = aesEncrypt(data, key, ivFinal)

        val output = ivFinal + encrypted

        return output.encodeBase64()
    }

    override fun decrypt(data: ByteArray, secret: String): String {

        val decoded = data.decodeBase64()

        val iv = decoded.copyOfRange(0, blockSize)

        val cipherText = decoded.copyOfRange(blockSize, decoded.size)

        val key = getAesKey()

        val decrypted = aesDecrypt(cipherText, key, iv)

        return decrypted.decodeToString()
    }

    private fun aesEncrypt(
        data: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray = commonCrypt(kCCEncrypt, data, key, iv)


    private fun aesDecrypt(
        data: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray = commonCrypt(kCCDecrypt, data, key, iv)


    @OptIn(ExperimentalForeignApi::class)
    private fun commonCrypt(
        operation: CCOperation,
        data: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray {

        val out = ByteArray(data.size + kCCBlockSizeAES128.toInt())

        val outLength = nativeHeap.alloc<size_tVar>()

        val status = key.usePinned { keyPinned ->

            iv.usePinned { ivPinned ->

                data.usePinned { dataPinned ->

                    out.usePinned { outPinned ->

                        CCCrypt(
                            operation,
                            kCCAlgorithmAES,
                            kCCOptionPKCS7Padding,
                            keyPinned.addressOf(0),
                            key.size.toULong(),
                            ivPinned.addressOf(0),
                            dataPinned.addressOf(0),
                            data.size.toULong(),
                            outPinned.addressOf(0),
                            out.size.toULong(),
                            outLength.ptr
                        )
                    }
                }
            }
        }

        if (status != kCCSuccess) {

            nativeHeap.free(outLength)

            throw IllegalStateException("CCCrypt failed status=$status")
        }

        val result = out.copyOf(outLength.value.toInt())

        nativeHeap.free(outLength)

        return result
    }

    private fun ByteArray.toHexLower(): String {

        val hexChars = "0123456789abcdef"

        return joinToString("") {

            val i = it.toInt() and 0xff

            "${hexChars[i shr 4]}${hexChars[i and 0x0f]}"
        }
    }


    // ✅ FIXED BASE64 ENCODE

    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.encodeBase64(): String {

        return this.usePinned {

            NSData.dataWithBytes(
                bytes = it.addressOf(0),
                length = size.toULong()
            ).base64EncodedStringWithOptions(0u)
        }
    }


    // ✅ FIXED BASE64 DECODE

    private fun ByteArray.decodeBase64(): ByteArray {

        val nsData = NSData.create(
            base64EncodedString = this.decodeToString(),
            options = 0u
        ) ?: error("Invalid Base64")

        return nsData.toByteArray()
    }


    // ✅ SAFE NSData → ByteArray

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {

        val size = length.toInt()

        val result = ByteArray(size)

        result.usePinned {

            memcpy(
                it.addressOf(0),
                bytes,
                length
            )
        }

        return result
    }
}