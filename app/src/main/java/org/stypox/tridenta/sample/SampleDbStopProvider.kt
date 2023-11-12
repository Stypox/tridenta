package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.CardinalPoint
import org.stypox.tridenta.enums.StopLineType

class SampleDbStopProvider : PreviewParameterProvider<DbStop> {
    override val values: Sequence<DbStop> = sequenceOf(
        DbStop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Sorni",
            street = "Sorni",
            town = "",
            type = StopLineType.Urban,
            wheelchairAccessible = false,
            cardinalPoint = CardinalPoint.North,
            isFavorite = false,
        ),
        DbStop(
            stopId = 1,
            latitude = 0.0,
            longitude = 0.0,
            name = "Funivia-Staz. di Monte-Sardagna lorem ipsum dolor sit amet",
            street = "Cembra - Via 4 Novembre - Dir.Cavalese lorem ipsum dolor sit",
            town = "Appiano sulla strada del vino",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
            cardinalPoint = null,
            isFavorite = true,
        ),
        DbStop(
            stopId = 2,
            latitude = 0.0,
            longitude = 0.0,
            name = "Pinè Bivio",
            street = "Civezzano-Loc.La Mochena",
            town = "Civezzano",
            type = StopLineType.Suburban,
            wheelchairAccessible = false,
            cardinalPoint = CardinalPoint.NorthWest,
            isFavorite = true,
        ),
        DbStop(
            stopId = 3,
            latitude = 0.0,
            longitude = 0.0,
            name = "Verona Big Center",
            street = "Verona \"Big Center\"",
            town = "Trento",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
            cardinalPoint = null,
            isFavorite = false,
        )
    )
}