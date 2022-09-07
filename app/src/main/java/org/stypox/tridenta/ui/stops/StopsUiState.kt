package org.stypox.tridenta.ui.stops

import org.stypox.tridenta.data.Stop

data class StopsUiState(
    val searchString: String,
    val stops: List<Stop>,
    val filteredStops: List<Stop>,
    val loading: Boolean
)