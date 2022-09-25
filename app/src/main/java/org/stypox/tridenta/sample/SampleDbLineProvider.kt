package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType

class SampleDbLineProvider : PreviewParameterProvider<DbLine> {
    override val values: Sequence<DbLine> = sequenceOf(
        DbLine(
            lineId = 396,
            area = Area.UrbanTrento,
            color = 0xc52720,
            longName = "Cortesano Gardolo P.Dante Villazzano 3",
            shortName = "3",
            type = StopLineType.Urban
        ),
        DbLine(
            lineId = 404,
            area = Area.UrbanTrento,
            color = 0x52332a,
            longName = "Centochiavi Piazza Dante Mattarello",
            shortName = "8",
            type = StopLineType.Urban
        ),
        DbLine(
            lineId = 466,
            area = Area.UrbanTrento,
            color = 0xbf6092,
            longName = "P.Dante Rosmini S.Rocco Povo Polo Soc.",
            shortName = "13",
            type = StopLineType.Urban
        ),
        DbLine(
            lineId = 415,
            area = Area.UrbanTrento,
            color = 0xe490b0,
            longName = "P.Dante Via Sanseverino Belvedere Ravina",
            shortName = "14",
            type = StopLineType.Urban
        ),
        DbLine(
            lineId = 0,
            area = Area.Suburban1,
            color = null,
            longName = "Cavalese - Masi di Cavalese",
            shortName = "B109",
            type = StopLineType.Suburban
        ),
        DbLine(
            lineId = 0,
            area = Area.Suburban2,
            color = null,
            longName = "Trento-Vezzano-Sarche-Tione",
            shortName = "B201",
            type = StopLineType.Suburban
        ),
    )
}