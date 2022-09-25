package org.stypox.tridenta.ui.lines

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.repo.LinesRepository
import org.stypox.tridenta.util.PreferenceKeys
import javax.inject.Inject

@HiltViewModel
class LinesViewModel @Inject constructor(
    application: Application,
    private val prefs: SharedPreferences,
    private val linesRepository: LinesRepository
) : AndroidViewModel(application) {

    private val mutableUiState =
        prefs.getInt(PreferenceKeys.LAST_SELECTED_AREA, -1) // see handling below
            .let { prefArea ->
                MutableStateFlow(
                    LinesUiState(
                        lines = listOf(),
                        // If the preferences do not contain a selected area, use DEFAULT_AREA and
                        // expand the header, so the user becomes aware that the header exists.
                        selectedArea = Area.values().firstOrNull { it.value == prefArea }
                            ?: Area.DEFAULT_AREA,
                        showAreaDialog = false,
                        loading = true
                    )
                )
            }
    val uiState = mutableUiState.asStateFlow()

    init {
        reloadLines()
    }

    fun setShowAreaDialog(headerExpanded: Boolean) {
        mutableUiState.update { linesUiState ->
            linesUiState.copy(showAreaDialog = headerExpanded)
        }
    }

    fun setSelectedArea(area: Area) {
        if (area == mutableUiState.value.selectedArea) {
            // close the header even if the user clicked on the same area item
            mutableUiState.update { linesUiState ->
                linesUiState.copy(selectedArea = area, showAreaDialog = false)
            }
        } else {
            // clear current lines and close the header, since the selected area item changed
            mutableUiState.update { linesUiState ->
                linesUiState.copy(selectedArea = area, lines = listOf(), showAreaDialog = false)
            }

            reloadLines()

            // save the last selected area to preferences
            prefs.edit().putInt(PreferenceKeys.LAST_SELECTED_AREA, area.value).apply()
        }
    }

    private fun reloadLines() {
        // show the loading indicator
        mutableUiState.update { linesUiState -> linesUiState.copy(loading = true) }

        viewModelScope.launch {
            val lines = withContext(Dispatchers.IO) {
                linesRepository.getDbLinesByArea(mutableUiState.value.selectedArea)
            }
            mutableUiState.update { linesUiState ->
                linesUiState.copy(lines = lines, loading = false)
            }
        }
    }
}