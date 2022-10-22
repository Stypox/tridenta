package org.stypox.tridenta.ui.stops

import org.stypox.tridenta.repo.data.UiStop

data class StopsUiState(
    val searchString: String,
    val stops: List<UiStop>,
    val loading: Boolean,
    val error: Boolean,
)