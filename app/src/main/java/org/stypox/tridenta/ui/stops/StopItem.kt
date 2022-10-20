package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.sample.SampleUiStopProvider
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.StopLineTypeIcon
import org.stypox.tridenta.ui.theme.TitleText
import org.stypox.tridenta.util.StringUtils.WORD_DELIMITERS_PATTERN
import org.stypox.tridenta.util.StringUtils.levenshteinDistance

private const val MIN_LEVENSHTEIN_DISTANCE = 3

@Preview(showBackground = true)
@Composable
fun StopItem(
    @PreviewParameter(SampleUiStopProvider::class) stop: UiStop,
    onLineClick: ((DbLine) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier.padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 16.dp),
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
                items(stop.lines) { line ->
                    LineShortName(
                        color = line.color,
                        shortName = line.shortName,
                        isFavorite = line.isFavorite,
                        modifier = onLineClick?.let { Modifier.clickable { it(line) } } ?: Modifier
                    )
                }
            }
        }

        Column {
            StopLineTypeIcon(stopLineType = stop.type)
            if (stop.wheelchairAccessible) {
                Icon(
                    imageVector = Icons.Filled.Accessible,
                    contentDescription = stringResource(R.string.accessible)
                )
            }
        }
    }
}
