package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import org.stypox.tridenta.util.StringUtils.WORD_DELIMITERS_PATTERN
import org.stypox.tridenta.util.StringUtils.levenshteinDistance

private const val MIN_LEVENSHTEIN_DISTANCE = 3

@Composable
fun StopItem(stop: Stop, modifier: Modifier = Modifier) {
    fun shouldBeShown(description: String): Boolean {
        val cleanDescription = WORD_DELIMITERS_PATTERN.matcher(description).replaceAll("")
        return cleanDescription.isNotEmpty() &&
                cleanDescription != "null" &&
                levenshteinDistance(description, stop.name) > MIN_LEVENSHTEIN_DISTANCE
    }

    val streetShown = shouldBeShown(stop.street)
    val townShown = shouldBeShown(stop.town) &&
            (!streetShown || levenshteinDistance(stop.town, stop.street) > MIN_LEVENSHTEIN_DISTANCE)

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .padding(end = 4.dp)
                .weight(1f)
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

        Column {
            Icon(
                imageVector = when (stop.type) {
                    StopLineType.Urban -> Icons.Filled.LocationCity
                    StopLineType.Suburban -> Icons.Filled.Landscape
                },
                contentDescription = null
            )
            if (stop.wheelchairAccessible) {
                Icon(
                    imageVector = Icons.Filled.Accessible,
                    contentDescription = null
                )
            }
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
            wheelchairAccessible = true,
            lines = listOf(
                Line(0, Area.DEFAULT_AREA, null, "", "B109", StopLineType.Urban, listOf()),
                Line(0, Area.DEFAULT_AREA, 0x123456, "", "8", StopLineType.Urban, listOf()),
                Line(0, Area.DEFAULT_AREA, 0xff3456, "", "13", StopLineType.Urban, listOf()),
            ),
        )
    )
}
