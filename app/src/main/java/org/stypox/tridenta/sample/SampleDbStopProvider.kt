package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.db.data.DbStop
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
        ),
        DbStop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Funivia-Staz. di Monte-Sardagna",
            street = "Cembra - Via 4 Novembre - Dir.Cavalese lorem ipsum dolor sit",
            town = "Appiano sulla strada del vino",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
        ),
        DbStop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Pinè Bivio",
            street = "Civezzano-Loc.La Mochena",
            town = "Civezzano",
            type = StopLineType.Suburban,
            wheelchairAccessible = false,
        ),
        DbStop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Verona Big Center",
            street = "Verona \"Big Center\"",
            town = "Trento",
            type = StopLineType.Suburban,
            wheelchairAccessible = true,
        )
    )
}