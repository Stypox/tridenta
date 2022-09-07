package org.stypox.tridenta.ui.stops

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.extractor.Extractor
import javax.inject.Inject

@HiltViewModel
class StopsViewModel @Inject constructor(
    application: Application,
    private val extractor: Extractor
) : AndroidViewModel(application) {

    private val mutableUiState = MutableStateFlow(
        StopsUiState(
            searchString = "",
            stops = listOf(),
            filteredStops = listOf(),
            loading = true
        )
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        reloadStops()
    }

    fun setSearchString(searchString: String) {
        // update the search string instantly, as it is shown in the search field
        mutableUiState.update { stopsUiState -> stopsUiState.copy(searchString = searchString) }
        updateFilteredStops()
    }

    private fun reloadStops() {
        // show the loading indicator
        mutableUiState.update { stopsUiState -> stopsUiState.copy(loading = true) }

        viewModelScope.launch {
            val stops = withContext(Dispatchers.IO) {
                extractor.getStops()
            }
            mutableUiState.update { stopsUiState ->
                stopsUiState.copy(
                    stops = stops,
                    loading = false
                )
            }
            updateFilteredStops()
        }
    }

    private fun updateFilteredStops() {
        // TODO use a better filtering and sorting method, that also caches nfkd-normalized strings
        viewModelScope.launch {
            val filteredStops = withContext(Dispatchers.Default) {
                val searchString = mutableUiState.value.searchString
                mutableUiState.value.stops
                    .filter { stop ->
                        arrayOf(stop.name, stop.street, stop.town).any {
                            it.contains(searchString, ignoreCase = true)
                        }
                    }
                    .sortedBy { stop ->
                        if (stop.name.contains(searchString, ignoreCase = true)) -2 else 0 +
                        if (stop.street.contains(searchString, ignoreCase = true)) -1 else 0 +
                        if (stop.town.contains(searchString, ignoreCase = true)) -1 else 0
                    }
            }
            mutableUiState.update { stopsUiState ->
                stopsUiState.copy(filteredStops = filteredStops)
            }
        }
    }
}