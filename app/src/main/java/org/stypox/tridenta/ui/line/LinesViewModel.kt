package org.stypox.tridenta.ui.line

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.extractor.Extractor
import javax.inject.Inject

@HiltViewModel
class LinesViewModel @Inject constructor(
    application: Application,
    private val extractor: Extractor
) : AndroidViewModel(application) {
    private val mutableUiState = MutableStateFlow(
        LinesUiState(
            lines = listOf(),
            selectedArea = Area.UrbanTrento,
            headerExpanded = false, // start with an unexpanded header to avoid visual issues
            loading = true // start with showing a loading indicator
        )
    )
    val uiState = mutableUiState.asStateFlow()

    init {

    }

    fun setHeaderExpanded(headerExpanded: Boolean) {
        mutableUiState.update { linesUiState ->
            linesUiState.copy(headerExpanded = headerExpanded)
        }
    }

    fun setSelectedArea(area: Area) {
        if (area == mutableUiState.value.selectedArea) {
            // close the header even if the user clicked on the same area item
            mutableUiState.update { linesUiState ->
                linesUiState.copy(selectedArea = area, headerExpanded = false)
            }
        } else {
            // clear current lines and close the header, since the selected area item changed
            mutableUiState.update { linesUiState ->
                linesUiState.copy(selectedArea = area, lines = listOf(), headerExpanded = false)
            }
            reloadLines()
        }
    }

    private fun reloadLines() {
        // show the loading indicator
        mutableUiState.update { linesUiState -> linesUiState.copy(loading = true) }

        viewModelScope.launch {
            val lines = withContext(Dispatchers.IO) {
                extractor.getLines(areas = arrayOf(mutableUiState.value.selectedArea))
            }
            mutableUiState.update { linesUiState ->
                linesUiState.copy(lines = lines)
            }
        }
    }
}