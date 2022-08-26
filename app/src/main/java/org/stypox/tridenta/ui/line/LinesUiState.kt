package org.stypox.tridenta.ui.line

import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line

data class LinesUiState(
    val lines: List<Line>,
    val selectedArea: Area,
    val headerExpanded: Boolean,
    val loading: Boolean,
)
