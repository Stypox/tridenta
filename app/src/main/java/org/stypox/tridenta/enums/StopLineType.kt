package org.stypox.tridenta.enums

import androidx.annotation.DrawableRes
import org.stypox.tridenta.R

enum class StopLineType(
    val value: String,
    @DrawableRes val shortcutDrawable: Int,
) {
    Urban("U", R.drawable.shortcut_urban),
    Suburban("E", R.drawable.shortcut_suburban),
}