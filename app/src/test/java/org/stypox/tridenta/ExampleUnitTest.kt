package org.stypox.tridenta

import okhttp3.OkHttpClient
import org.junit.Test
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.extractor.HttpClient
import java.time.ZonedDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val extractor = Extractor(HttpClient(OkHttpClient()))
        println(extractor.getStops().first())
        println(extractor.getLines().first())
    }
    @Test
    fun addition_isCorrect2() {
        val extractor = Extractor(HttpClient(OkHttpClient()))
        println(extractor.getTripsByStop(247, StopLineType.Urban, ZonedDateTime.now(), 1))
    }
}