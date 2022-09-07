package org.stypox.tridenta.util

/**
 * Calculates the Levenshtein distance between the two strings in O(n²) time and O(n) memory.
 * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Wikipedia</a>
 * @see <a href="https://github.com/Stypox/dicio-android/blob/c2bdb04fd6bad4399ced3583b4518dd78d033bbd/app/src/main/java/org/dicio/dicio_android/util/StringUtils.java#L74">
 *     Adapted from Dicio's implementation</a>
 */
fun levenshteinDistance(a: String, b: String): Int {
    // TODO add tests
    val memory = IntArray(a.length + 1) { it }
    var itemOnLeftBeforeOverwriting = 0

    for (i in a.indices) {
        for (j in b.indices) {
            val substitutionCost = if (a[i].lowercaseChar() == b[j].lowercaseChar()) 0 else 1

            memory[i + 1].let {
                memory[i + 1] = minOf(
                    memory[i] + 1, // calculated in previous iteration
                    memory[i + 1] + 1,
                    itemOnLeftBeforeOverwriting + substitutionCost
                )
                itemOnLeftBeforeOverwriting = it
            }
        }
    }

    return memory.last()
}