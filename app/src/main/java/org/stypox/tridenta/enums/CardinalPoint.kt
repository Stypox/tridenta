package org.stypox.tridenta.enums

import androidx.annotation.StringRes
import org.stypox.tridenta.R

enum class CardinalPoint(@StringRes val shortName: Int) {
    East(R.string.east_short),
    NorthEast(R.string.north_east_short),
    North(R.string.north_short),
    NorthWest(R.string.north_west_short),
    West(R.string.west_short),
    SouthWest(R.string.south_west_short),
    South(R.string.south_short),
    SouthEast(R.string.south_east_short),
}