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
import org.stypox.tridenta.repo.StopsRepository
import javax.inject.Inject

@HiltViewModel
class StopsViewModel @Inject constructor(
    application: Application,
    private val stopsRepository: StopsRepository
) : AndroidViewModel(application) {

    private val mutableUiState = MutableStateFlow(
        StopsUiState(
            searchString = "",
            stops = listOf(),
            loading = true
        )
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        reloadStops(forceReload = false)
    }

    fun setSearchString(searchString: String) {
        mutableUiState.update { it.copy(searchString = searchString) }
        reloadStops(forceReload = false)
    }

    fun onReload() {
        reloadStops(forceReload = true)
    }

    private fun reloadStops(forceReload: Boolean) {
        // update the search string instantly, as it is shown in the search field
        mutableUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val filteredStops = withContext(Dispatchers.Default) {
                // TODO implement proper paging if needed
                //  https://developer.android.com/jetpack/compose/lists#large-datasets
                stopsRepository.getUiStopsFiltered(
                    mutableUiState.value.searchString,
                    100,
                    0,
                    forceReload
                )
            }
            mutableUiState.update { it.copy(stops = filteredStops, loading = false) }
        }
    }
}