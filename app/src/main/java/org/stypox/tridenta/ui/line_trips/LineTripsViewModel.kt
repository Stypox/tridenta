package org.stypox.tridenta.ui.line_trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.db.HistoryDao
import org.stypox.tridenta.repo.LineTripsRepository
import org.stypox.tridenta.repo.LinesRepository
import org.stypox.tridenta.ui.navArgs
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class LineTripsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val linesRepository: LinesRepository,
    private val tripsRepository: LineTripsRepository,
    private val historyDao: HistoryDao
) : AndroidViewModel(application) {

    private val navArgs = savedStateHandle.navArgs<LineTripsScreenNavArgs>()

    private val mutableUiState = MutableStateFlow(
        LineTripsUiState(
            line = null,
            tripsInDayCount = 0,
            tripIndex = 0,
            trip = null,
            prevEnabled = false,
            nextEnabled = false,
            referenceDateTime = ZonedDateTime.now(),
            stopIdToHighlight = navArgs.stopIdToHighlight,
            stopTypeToHighlight = navArgs.stopTypeToHighlight,
            loading = true
        )
    )
    val uiState = mutableUiState.asStateFlow()

    val isFavorite = historyDao.isFavorite(
        isLine = true,
        id = navArgs.lineId,
        type = navArgs.lineType
    )

    init {
        loadLine()
        setReferenceDateTime(ZonedDateTime.now())
    }

    private fun loadLine() {
        viewModelScope.launch {
            val line = withContext(Dispatchers.IO) {
                linesRepository.getUiLine(navArgs.lineId, navArgs.lineType).also {
                    // register a view for this line (assuming loadLine is called once)
                    historyDao.registerAccessed(true, navArgs.lineId, navArgs.lineType)
                }
            }
            mutableUiState.update { it.copy(line = line) }
        }
    }

    fun setReferenceDateTime(referenceDateTime: ZonedDateTime) {
        mutableUiState.update {
            it.copy(
                tripsInDayCount = 0,
                tripIndex = 0,
                trip = null,
                prevEnabled = false,
                nextEnabled = false,
                referenceDateTime = referenceDateTime,
                loading = true
            )
        }

        viewModelScope.launch {
            val (tripsInDayCount, tripIndex, trip) = withContext(Dispatchers.IO) {
                tripsRepository.getUiTrip(
                    lineId = navArgs.lineId,
                    lineType = navArgs.lineType,
                    referenceDateTime = referenceDateTime
                )
            }

            mutableUiState.update {
                it.copy(
                    tripsInDayCount = tripsInDayCount,
                    tripIndex = tripIndex,
                    trip = trip,
                    prevEnabled = tripIndex > 0,
                    nextEnabled = tripIndex < tripsInDayCount - 1,
                    referenceDateTime = referenceDateTime,
                    loading = false
                )
            }
        }
    }

    fun onReload() {
        val previousTrip = uiState.value.trip ?: return
        mutableUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                tripsRepository.reloadUiTrip(
                    uiTrip = previousTrip,
                    index = uiState.value.tripIndex,
                    referenceDateTime = uiState.value.referenceDateTime
                )
            }
            mutableUiState.update { it.copy(trip = trip, loading = false) }
        }
    }

    fun onPrevClicked() {
        loadIndex(uiState.value.tripIndex - 1)
    }

    fun onNextClicked() {
        loadIndex(uiState.value.tripIndex + 1)
    }

    private fun loadIndex(index: Int) {
        if (index < 0 || index >= uiState.value.tripsInDayCount) {
            mutableUiState.update { it.copy(loading = false) }
            return // this will happen when there are no trips in a day, for example
        }

        mutableUiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                tripsRepository.getUiTrip(
                    lineId = navArgs.lineId,
                    lineType = navArgs.lineType,
                    referenceDateTime = uiState.value.referenceDateTime,
                    index = index
                )
            }

            mutableUiState.update {
                it.copy(
                    loading = false,
                    tripIndex = index,
                    trip = trip,
                    prevEnabled = index > 0,
                    nextEnabled = index < uiState.value.tripsInDayCount - 1,
                )
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                historyDao.setFavorite(
                    isLine = true,
                    id = navArgs.lineId,
                    type = navArgs.lineType,
                    isFavorite = isFavorite.value?.not() ?: true
                )
            }
        }
    }
}