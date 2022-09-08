package org.stypox.tridenta.ui.lines

import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line

data class LinesUiState(
    val lines: List<Line>,
    val selectedArea: Area,
    val showAreaDialog: Boolean,
    val loading: Boolean,
)
