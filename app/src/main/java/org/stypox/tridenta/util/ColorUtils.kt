package org.stypox.tridenta.util

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color


fun Int.toComposeColor(): Color {
    return Color(0xff000000 + this)
}

/**
 * @return converts the int color to a Compose color, or returns the default color for bus lines
 * when the the color is null
 */
fun Int?.toLineColor(): Color {
    return if (this == null)
        Color.LightGray
    else
        Color(0xff000000 + this)
}

/**
 * @return one of [Color.Black] or [Color.White], such that text with such color is readable on
 * [backgroundColor].
 * @see <a href="https://stackoverflow.com/a/3943023/9481500">Stack Overflow</a>
 */
fun textColorOnBackground(@ColorInt backgroundColor: Color): Color {
    return backgroundColor.run {
        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 0.5)
            Color.Black
        else
            Color.White
    }
}