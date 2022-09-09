package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.data.Stop
import org.stypox.tridenta.data.StopLineType

class SampleStopProvider : PreviewParameterProvider<Stop> {
    private val sampleLines = SampleLineProvider().values

    override val values: Sequence<Stop> = sequenceOf(
        Stop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Sorni",
            street = "Sorni",
            town = "",
            type = StopLineType.Urban,
            wheelchairAccessible = false,
            lines = listOf()
        ),
        Stop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Funivia-Staz. di Monte-Sardagna",
            street = "Cembra - Via 4 Novembre - Dir.Cavalese lorem ipsum dolor sit",
            town = "Appiano sulla strada del vino",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
            lines = sampleLines.toList(),
        ),
        Stop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Pinè Bivio",
            street = "Civezzano-Loc.La Mochena",
            town = "Civezzano",
            type = StopLineType.Suburban,
            wheelchairAccessible = false,
            lines = List(10) { sampleLines.last() },
        ),
        Stop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Verona Big Center",
            street = "Verona \"Big Center\"",
            town = "Trento",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
            lines = (1 until 5).flatMap { sampleLines },
        )
    )
}