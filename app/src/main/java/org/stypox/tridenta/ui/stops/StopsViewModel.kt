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
        setSearchString("")
    }

    fun setSearchString(searchString: String) {
        // update the search string instantly, as it is shown in the search field
        mutableUiState.update { it.copy(searchString = searchString, loading = true) }
        viewModelScope.launch {
            val filteredStops = withContext(Dispatchers.Default) {
                // TODO implement limit and offset
                stopsRepository.getUiStopsFiltered(searchString, 100, 0)
            }
            mutableUiState.update { stopsUiState ->
                stopsUiState.copy(stops = filteredStops)
            }
        }
    }
}