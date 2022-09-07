package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line
import org.stypox.tridenta.data.Stop
import org.stypox.tridenta.data.StopLineType
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.TitleText
import org.stypox.tridenta.util.StringUtils.levenshteinDistance

private const val MIN_LEVENSHTEIN_DISTANCE = 3

@Composable
fun StopItem(stop: Stop, modifier: Modifier = Modifier) {
    val streetShown = stop.street.isNotEmpty() &&
            levenshteinDistance(stop.street, stop.name) > MIN_LEVENSHTEIN_DISTANCE
    val townShown = stop.town.isNotEmpty() &&
            levenshteinDistance(stop.town, stop.name) > MIN_LEVENSHTEIN_DISTANCE &&
            (!streetShown || levenshteinDistance(stop.town, stop.street) > MIN_LEVENSHTEIN_DISTANCE)
    val paddingModifier = modifier.padding(16.dp)

    Column(
        modifier = paddingModifier
    ) {
        TitleText(text = stop.name)
        if (streetShown) {
            BodyText(
                text = stop.street,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (townShown) {
            BodyText(
                text = stop.town,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            items(stop.lines) { line -> LineShortName(line) }
        }
    }
}

@Preview
@Composable
fun StopItemPreview() {
    StopItem(
        stop = Stop(
            stopId = 0,
            latitude = 0.0,
            longitude = 0.0,
            name = "Funivia-Staz. di Monte-Sardagna",
            street = "Cembra - Via 4 Novembre - Dir.Cavalese lorem ipsum dolor sit",
            town = "Appiano sulla strada del vino",
            type = StopLineType.Suburban,
            lines = listOf(
                Line(0, Area.DEFAULT_AREA, null, "", "B109", StopLineType.Urban, listOf()),
                Line(0, Area.DEFAULT_AREA, 0x123456, "", "8", StopLineType.Urban, listOf()),
                Line(0, Area.DEFAULT_AREA, 0xff3456, "", "13", StopLineType.Urban, listOf()),
            ),
        )
    )
}
