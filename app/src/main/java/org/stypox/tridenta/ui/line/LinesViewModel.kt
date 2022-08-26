package org.stypox.tridenta.ui.line

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.shortNameComparator
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.ui.pref.PreferenceKeys
import javax.inject.Inject

@HiltViewModel
class LinesViewModel @Inject constructor(
    application: Application,
    private val extractor: Extractor
) : AndroidViewModel(application) {
    private val mutableUiState =
        PreferenceManager.getDefaultSharedPreferences(application)
            .getInt(PreferenceKeys.LAST_SELECTED_AREA, -1) // see handling below
            .let { prefArea ->
                MutableStateFlow(
                    LinesUiState(
                        lines = listOf(),
                        // If the preferences do not contain a selected area, use DEFAULT_AREA and
                        // expand the header, so the user becomes aware that the header exists.
                        selectedArea = Area.values().firstOrNull { it.value == prefArea }
                            ?: Area.DEFAULT_AREA,
                        headerExpanded = Area.values().all { it.value != prefArea },
                        loading = true
                    )
                )
            }
    val uiState = mutableUiState.asStateFlow()

    init {
        reloadLines()
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

            // save the last selected area to preferences
            PreferenceManager.getDefaultSharedPreferences(getApplication())
                .edit()
                .putInt(PreferenceKeys.LAST_SELECTED_AREA, area.value)
                .apply()
        }
    }

    private fun reloadLines() {
        // show the loading indicator
        mutableUiState.update { linesUiState -> linesUiState.copy(loading = true) }

        viewModelScope.launch {
            val lines = withContext(Dispatchers.IO) {
                extractor.getLines(areas = arrayOf(mutableUiState.value.selectedArea))
                    .sortedWith(::shortNameComparator)
            }
            mutableUiState.update { linesUiState ->
                linesUiState.copy(lines = lines, loading = false)
            }
        }
    }
}