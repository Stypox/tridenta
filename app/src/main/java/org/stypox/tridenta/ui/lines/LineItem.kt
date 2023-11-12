package org.stypox.tridenta.ui.lines

import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.util.textColorOnBackground
import org.stypox.tridenta.util.toLineColor

@Composable
fun LineItem(line: DbLine, showAreaChip: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LineShortName(color = line.color, shortName = line.shortName, isFavorite = line.isFavorite)

        BodyText(
            text = line.longName,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1.0f)
        )

        if (showAreaChip) {
            AreaChip(
                area = line.area,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun LineItemWithAreaPreview(@PreviewParameter(SampleDbLineProvider::class) line: DbLine) {
    LineItem(line = line, showAreaChip = true)
}

@Preview
@Composable
private fun LineItemNoAreaPreview(@PreviewParameter(SampleDbLineProvider::class) line: DbLine) {
    LineItem(line = line, showAreaChip = false)
}

@Composable
fun LineShortName(
    @ColorInt color: Int?,
    shortName: String,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = color.toLineColor()
    val textColor = textColorOnBackground(backgroundColor)

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        ) {

            LabelText(
                text = shortName,
                color = textColor,
                maxLines = 1,
            )

            if (isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.favorite),
                    modifier = Modifier.padding(start = 2.dp).size(12.dp),
                    tint = textColor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LineShortNamePreview(@PreviewParameter(SampleDbLineProvider::class) line: DbLine) {
    LineShortName(color = line.color, shortName = line.shortName, isFavorite = line.isFavorite)
}
