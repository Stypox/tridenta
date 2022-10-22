package org.stypox.tridenta.ui.lines

import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area

data class LinesUiState(
    val lines: List<DbLine>,
    val selectedArea: Area,
    val loading: Boolean,
    val error: Boolean,
)
