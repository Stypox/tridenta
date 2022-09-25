package org.stypox.tridenta.ui.lines

import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.util.textColorOnBackground
import org.stypox.tridenta.util.toComposeColor

@Preview
@Composable
fun LineItem(
    @PreviewParameter(SampleDbLineProvider::class) line: DbLine,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LineShortName(color = line.color, shortName = line.shortName)

        BodyText(
            text = line.longName,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun LineShortName(@ColorInt color: Int?, shortName: String, modifier: Modifier = Modifier) {
    val backgroundColor = color.toComposeColor()

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        LabelText(
            text = shortName,
            color = textColorOnBackground(backgroundColor),
            modifier = Modifier.padding(8.dp, 4.dp),
            maxLines = 1,
        )
    }
}
