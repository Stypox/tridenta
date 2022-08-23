package org.stypox.tridenta.data

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChairAlt
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import org.stypox.tridenta.R

enum class Area(
    val value: Int,
    @StringRes val nameRes: Int,
    @ColorInt val color: Int,
    val icon: ImageVector,
) {
    Suburban1(1, R.string.area_suburban1, 0xc71585, Icons.Filled.Landscape),
    Suburban2(2, R.string.area_suburban2, 0xb8860b, Icons.Filled.Landscape),
    Suburban3(3, R.string.area_suburban3, 0x191970, Icons.Filled.Landscape),
    Suburban4(4, R.string.area_suburban4, 0x228b22, Icons.Filled.Landscape),
    Suburban5(5, R.string.area_suburban5, 0x7f0000, Icons.Filled.Landscape),
    Suburban6(6, R.string.area_suburban6, 0x008080, Icons.Filled.Landscape),
    Railway(7, R.string.area_railway, 0xcc0000, Icons.Filled.Train),
    Funicular(8, R.string.area_funicular, 0x0000cc, Icons.Filled.ChairAlt),
    UrbanPergine(21, R.string.area_urban_pergine, 0x000000, Icons.Filled.LocationCity),
    UrbanAltoGarda(22, R.string.area_urban_alto_garda, 0x000000, Icons.Filled.LocationCity),
    UrbanTrento(23, R.string.area_urban_trento, 0x000000, Icons.Filled.LocationCity),
    UrbanRovereto(24, R.string.area_urban_rovereto, 0x000000, Icons.Filled.LocationCity),
}