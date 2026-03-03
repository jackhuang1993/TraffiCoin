package com.example.trafficoin.manager.media

import android.content.Context
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

/**
 * @author Jack
 */
class ImageDownloader(
    private val context: Context,
    private val client: OkHttpClient
) {
    private val mDigest: MessageDigest by lazy { MessageDigest.getInstance("SHA-256") }


    suspend fun download(url: String): File? {
        val dir = File(context.cacheDir, "pics")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, url.toSha256())
        return file.takeIf { it.exists() }
            ?: retry { downloadToFile(url, file) }
    }

    private fun String.toSha256(): String {
        return mDigest.digest(encodeToByteArray())
            .joinToString(separator = "", transform = "%02x"::format)
    }

    private suspend fun downloadToFile(url: String, target: File): File? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null

                    val body = response.body
                    target.outputStream().use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    target
                }
            }.onFailure { it.printStackTrace() }
                .getOrNull()
        }
    }

    private suspend fun <T> retry(
        times: Int = 3,
        initialDelay: Long = 1000,
        block: suspend () -> T?
    ): T? {
        repeat(times) { attempt ->
            val result = block()
            if (result != null) return result

            // 線性退避
            if (attempt < times - 1)
                delay(initialDelay * (attempt + 1))
        }
        return null
    }
}