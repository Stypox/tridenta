package org.stypox.tridenta.ui.lines

import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line
import org.stypox.tridenta.data.StopLineType
import org.stypox.tridenta.ui.theme.AppTheme

@Composable
fun LineItem(line: Line, modifier: Modifier = Modifier) {
    val backgroundColor = if (line.color == null)
        Color.LightGray
    else
        Color(0xff000000 + line.color)

    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = backgroundColor,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = line.shortName,
                color = textColorOnBackground(backgroundColor),
                modifier = Modifier.padding(8.dp, 4.dp)
            )
        }

        Text(
            text = line.longName,
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)
        )
    }
}

@Preview
@Composable
fun LineItemPreview() {
    AppTheme {
        Column {
            LineItem(
                line = Line(
                    0,
                    Area.Suburban2,
                    null,
                    "Trento-Vezzano-Sarche-Tione",
                    "B201",
                    StopLineType.Suburban,
                    listOf()
                ),
            )
            LineItem(
                line = Line(
                    0,
                    Area.UrbanTrento,
                    0xe490b0,
                    "P.Dante Via Sanseverino Belvedere Ravina",
                    "14",
                    StopLineType.Urban,
                    listOf()
                ),
            )
            LineItem(
                line = Line(
                    0,
                    Area.UrbanTrento,
                    0x52332a,
                    "Centochiavi Piazza Dante Mattarello",
                    "8",
                    StopLineType.Urban,
                    listOf()
                ),
            )
        }
    }
}

/**
 * Returns one of [Color.Black] or [Color.White], such that text with such color is readable on
 * [backgroundColor].
 * @see <a href="https://stackoverflow.com/a/3943023/9481500">Stack Overflow</a>
 */
private fun textColorOnBackground(@ColorInt backgroundColor: Color): Color {
    return backgroundColor.run {
        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 0.5)
            Color.Black
        else
            Color.White
    }
}