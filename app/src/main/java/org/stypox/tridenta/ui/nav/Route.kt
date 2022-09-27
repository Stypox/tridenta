package org.stypox.tridenta.ui.nav

import androidx.navigation.NavType
import org.stypox.tridenta.enums.StopLineType

class Route(private val basePath: String, private val arguments: List<Argument<*>> = listOf()) {
    class Argument<T>(val name: String, val type: NavType<T>)

    fun parametrizedString(): String {
        return basePath + arguments.joinToString { argument -> "/{${argument.name}}" }
    }

    fun build(builderAction: MutableMap<Argument<*>, String>.() -> Unit): String {
        val argumentValueMap = buildMap(builderAction)
        return basePath + arguments.joinToString { argument ->
            val argumentValue = argumentValueMap[argument]
                ?: throw IllegalArgumentException("Missing value for argument $argument")
            "/$argumentValue"
        }
    }

    companion object {
        val t = buildMap<String, String> { "" to ""; "e" to "c" }
        val LINES = Route("lines")
        val STOPS = Route("stops")
        val ARG_LINE_ID = Argument("lineId", NavType.IntType)
        val ARG_LINE_TYPE = Argument("lineType", NavType.EnumType(StopLineType::class.java))
        val LINE_TRIPS = Route("lineTrips", listOf(ARG_LINE_ID, ARG_LINE_TYPE))
    }
}
