package org.stypox.tridenta.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color.HSVToColor
import android.graphics.Color.colorToHSV
import androidx.annotation.ColorInt
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.theme.AppTheme

private data class ZoneNumberPosition(
    val number: Int,
    @ColorInt val color: Int,
    val x: Float,
    val y: Float,
)

private val ZONE_NUMBERS = listOf(
    ZoneNumberPosition(1, 0xc71585, .81f, .21f),
    ZoneNumberPosition(2, 0xb8860b, .19f, .59f),
    ZoneNumberPosition(3, 0x191970, .42f, .78f),
    ZoneNumberPosition(4, 0x228b22, .66f, .50f),
    ZoneNumberPosition(5, 0x7f0000, .90f, .38f),
    ZoneNumberPosition(6, 0x008080, .33f, .20f),
)

@Composable
fun SuburbanZonesMap(modifier: Modifier = Modifier, onZoneClick: (Int) -> Unit) {
    BoxWithConstraints(modifier = modifier) {
        val painter = painterResource(id = R.drawable.zones)
        // enforce aspect ratio while maximizing width
        val height = this.maxWidth * painter.intrinsicSize.height / painter.intrinsicSize.width
        // the text should be sized with respect to the image size, to avoid overlaps
        val textSize = with(LocalDensity.current) {
            (this@BoxWithConstraints.maxWidth / 7).toSp()
        }

        Image(
            painter = painter,
            contentDescription = stringResource(R.string.map_of_trentino),
            modifier = Modifier.size(this.maxWidth, height)
        )

        ZONE_NUMBERS.forEach { zoneNumber ->
            val color = Color(
                0xff000000 + if (isSystemInDarkTheme()) {
                    val hsvColor = FloatArray(3)
                    colorToHSV(zoneNumber.color, hsvColor)
                    // increase luminance (i.e. hsvColor[2]) to improve contrast
                    hsvColor[2] = 1.0f - (1.0f - hsvColor[2]) / 3
                    HSVToColor(hsvColor)
                } else {
                    zoneNumber.color
                }
            )

            Text(
                text = "${zoneNumber.number}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = textSize,
                    color = color
                ),
                modifier = Modifier
                    .clickable { onZoneClick(zoneNumber.number) }
                    .align { zone, space, _ ->
                        IntOffset(
                            (space.width * zoneNumber.x - zone.width / 2).toInt(),
                            (space.height * zoneNumber.y - zone.height / 2).toInt()
                        )
                    }
            )
        }
    }
}

// ensure it scales correctly both at small and high sizes
@Preview(name = "Suburban zones map, small light", uiMode = UI_MODE_NIGHT_NO, widthDp = 200)
@Preview(name = "Suburban zones map, medium dark", uiMode = UI_MODE_NIGHT_YES, widthDp = 500)
@Preview(name = "Suburban zones map, big light", uiMode = UI_MODE_NIGHT_NO, widthDp = 800)
@Composable
fun SuburbanZonesMapPreview() {
    AppTheme {
        SuburbanZonesMap { }
    }
}