package org.stypox.tridenta.ui.line

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
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = if (line.color == null) Color.LightGray else Color(0xff000000 + line.color),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = line.shortName,
                color = Color.Black,
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
        LineItem(
            line = Line(
                0,
                Area.UrbanTrento,
                null,
                "Trento-Vezzano-Sarche-Tione",
                "B201",
                StopLineType.Urban,
                listOf()
            ),
        )
    }
}