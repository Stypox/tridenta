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
import org.stypox.tridenta.log.logError
import org.stypox.tridenta.repo.LinesRepository
import org.stypox.tridenta.util.PreferenceKeys
import javax.inject.Inject
import androidx.core.content.edit
import org.stypox.tridenta.repo.StopLineReloadHandler

@HiltViewModel
class LinesViewModel @Inject constructor(
    application: Application,
    private val prefs: SharedPreferences,
    private val linesRepository: LinesRepository,
    private val stopLineReloadHandler: StopLineReloadHandler,
) : AndroidViewModel(application) {

    private val mutableUiState =
        prefs.getInt(PreferenceKeys.LAST_SELECTED_AREA, -1) // see handling below
            .let { prefArea ->
                MutableStateFlow(
                    LinesUiState(
                        lines = listOf(),
                        // if the preferences do not contain a selected area, use DEFAULT_AREA
                        selectedArea = Area.entries.firstOrNull { it.value == prefArea }
                            ?: Area.DEFAULT_AREA,
                        loading = true,
                        error = false,
                    )
                )
            }
    val uiState = mutableUiState.asStateFlow()

    val lastReloadWasError get() = stopLineReloadHandler.lastReloadWasError

    init {
        reloadLines(forceReload = false)
    }

    fun setSelectedArea(area: Area) {
        if (area != mutableUiState.value.selectedArea) {
            // clear current lines and close the header, since the selected area item changed
            mutableUiState.update { it.copy(selectedArea = area, lines = listOf()) }

            reloadLines(forceReload = false)

            // save the last selected area to preferences
            prefs.edit { putInt(PreferenceKeys.LAST_SELECTED_AREA, area.value) }
        }
    }

    fun onReload() {
        reloadLines(forceReload = true)
    }

    private fun reloadLines(forceReload: Boolean) {
        // show the loading indicator
        mutableUiState.update { it.copy(loading = true, error = false) }

        viewModelScope.launch {
            val lines = withContext(Dispatchers.IO) {
                try {
                    linesRepository.getDbLinesByArea(uiState.value.selectedArea, forceReload)
                } catch (e: Throwable) {
                    logError("Could not load lines in area ${uiState.value.selectedArea}", e)
                    null
                }
            }

            mutableUiState.update {
                it.copy(
                    lines = lines ?: listOf(),
                    loading = false,
                    error = lines == null,
                )
            }
        }
    }
}