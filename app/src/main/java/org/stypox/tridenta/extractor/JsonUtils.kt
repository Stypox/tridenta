package org.stypox.tridenta.extractor

import org.json.JSONArray

inline fun <reified T, R> JSONArray.map(transform: (T) -> R): List<R> {
    return (0 until length())
        .mapNotNull { get(it) as? T }
        .map(transform)
}