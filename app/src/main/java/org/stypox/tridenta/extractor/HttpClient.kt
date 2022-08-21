package org.stypox.tridenta.extractor

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import javax.inject.Inject

class HttpClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val retries: Int = 5
) {

    fun fetchJson(url: String): String {
        val request = Request.Builder()
            .get()
            .url(url)
            .header("Accept", "application/json")
            .header("Authorization", "Basic bWl0dG1vYmlsZTplY0dzcC5SSEIz")
            .build()

        lateinit var error: SocketTimeoutException
        repeat(retries) {
            try {
                return okHttpClient.newCall(request).execute().use { it.body!!.string() }
            } catch (e: SocketTimeoutException) {
                // since the server seems to be unstable, retry [retries] times on timeout
                error = e
            }
        }

        // after [retries] tries, throw the error
        throw error
    }
}