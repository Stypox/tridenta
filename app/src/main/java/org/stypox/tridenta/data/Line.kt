package org.stypox.tridenta.data

import androidx.annotation.ColorInt
import java.time.OffsetDateTime
import java.util.regex.Pattern
import kotlin.math.min

data class Line(
    val lineId: Int,
    val area: Area?,
    @ColorInt val color: Int?,
    val longName: String,
    val shortName: String,
    val type: StopLineType,
    val newsItems: List<NewsItem>,
)

data class NewsItem(
    val serviceType: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val header: String,
    val details: String,
    val url: String,
    val affectedLineIds: List<Int>,
)

private val SHORT_NAME_SPLIT_PATTERN = Pattern.compile("[^0-9]+|[0-9]+")

private fun splitShortName(shortName: String): ArrayList<Any> {
    val matcher = SHORT_NAME_SPLIT_PATTERN.matcher(shortName)
    val result = ArrayList<Any>()
    while (matcher.find()) {
        result.add(try {
            matcher.group().toInt()
        } catch (e: NumberFormatException) {
            matcher.group()
        })
    }
    return result
}

/**
 * Compares two lines by their short name, such that a list of lines would be sorted
 * lexicographically except for the fact that numbers are parsed as such and then considered as a
 * whole (and not as independent chars). So e.g. 2 < 13; A51 < B401; ...
 */
fun shortNameComparator(a: Line, b: Line): Int {
    val aSplit = splitShortName(a.shortName)
    val bSplit = splitShortName(b.shortName)

    for (i in 0 until min(aSplit.size, bSplit.size)) {
        if (aSplit[i] is String) {
            if (bSplit[i] is String) {
                if (aSplit[i] != bSplit[i]) {
                    return (aSplit[i] as String).compareTo((bSplit[i] as String))
                }
                // else continue to next piece
            } else /* bSplit[i] is Int */ {
                return 1 // numbers are before letters
            }

        } else /* aSplit[i] is Int */ {
            if (bSplit[i] is String) {
                return -1 // letters are after numbers
            } else /* bSplit[i] is Int */ {
                if (aSplit[i] != bSplit[i]) {
                    return (aSplit[i] as Int).compareTo((bSplit[i] as Int))
                }
                // else continue to next piece
            }
        }
    }

    // the first `min(aSplit.size, bSplit.size)` items are equal, now let's just compare sizes
    return aSplit.size.compareTo(bSplit.size)
}