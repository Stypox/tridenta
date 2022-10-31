package org.stypox.tridenta.extractor

import okhttp3.OkHttpClient
import okhttp3.Request
import org.stypox.tridenta.log.logWarning
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val retries: Int = 5

    fun fetchJson(url: String): String {
        val request = Request.Builder()
            .get()
            .url(url)
            .header("Accept", "application/json")
            .header("Authorization", "Basic bWl0dG1vYmlsZTplY0dzcC5SSEIz")
            .build()

        var error: Throwable? = null
        repeat(retries) { retryIndex ->
            try {
                return okHttpClient.newCall(request)
                    .execute()
                    .use { it.body!!.string() }
                    .also {
                        error?.let {
                            // only log if at the end we were able to make the request properly
                            logWarning("Ignoring network error: " + it::class.qualifiedName)
                        }
                    }
            } catch (e: Throwable) {
                // since the server seems to be unstable, retry [retries] times on timeout
                if (retryIndex + 1 != retries) {
                    Thread.sleep(200L * (retryIndex + 1)) // will wait at most 0.2*(1+2+3+4)=2s
                }
                error = e
            }
        }

        // after [retries] tries, throw the error
        throw error ?: java.lang.NullPointerException("Unreachable code")
    }
}