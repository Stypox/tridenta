package org.stypox.tridenta.ui.lines

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color.HSVToColor
import android.graphics.Color.colorToHSV
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.ui.theme.AppTheme

private data class AreaPosition(
    val area: Area,
    val x: Float,
    val y: Float,
)

private val AREA_POSITIONS = listOf(
    AreaPosition(Area.Suburban1, .81f, .21f),
    AreaPosition(Area.Suburban2, .19f, .59f),
    AreaPosition(Area.Suburban3, .42f, .78f),
    AreaPosition(Area.Suburban4, .66f, .50f),
    AreaPosition(Area.Suburban5, .90f, .38f),
    AreaPosition(Area.Suburban6, .33f, .20f),
)

@Composable
fun SuburbanAreasMap(modifier: Modifier = Modifier, onAreaClick: (Area) -> Unit) {
    BoxWithConstraints(modifier = modifier) {
        val painter = painterResource(id = R.drawable.areas)
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

        AREA_POSITIONS.forEach { areaPosition ->
            val color = Color(
                0xff000000 + if (isSystemInDarkTheme()) {
                    val hsvColor = FloatArray(3)
                    colorToHSV(areaPosition.area.color, hsvColor)
                    // increase luminance (i.e. hsvColor[2]) to improve contrast
                    hsvColor[2] = 1.0f - (1.0f - hsvColor[2]) / 3
                    HSVToColor(hsvColor)
                } else {
                    areaPosition.area.color
                }
            )

            Text(
                text = "${areaPosition.area.value}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = textSize,
                    color = color
                ),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = { onAreaClick(areaPosition.area) }
                    )
                    .align { zone, space, _ ->
                        IntOffset(
                            (space.width * areaPosition.x - zone.width / 2).toInt(),
                            (space.height * areaPosition.y - zone.height / 2).toInt()
                        )
                    }
                    .padding(this@BoxWithConstraints.maxWidth / 16, 0.dp)
            )
        }
    }
}

// ensure it scales correctly both at small and high sizes
@Preview(name = "Suburban areas map, small light", uiMode = UI_MODE_NIGHT_NO, widthDp = 200)
@Preview(name = "Suburban areas map, medium dark", uiMode = UI_MODE_NIGHT_YES, widthDp = 500)
@Preview(name = "Suburban areas map, big light", uiMode = UI_MODE_NIGHT_NO, widthDp = 800)
@Composable
fun SuburbanAreasMapPreview() {
    AppTheme {
        SuburbanAreasMap { }
    }
}