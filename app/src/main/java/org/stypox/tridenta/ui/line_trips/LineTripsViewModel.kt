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
import org.stypox.tridenta.log.logError
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
            loading = true,
            error = false,
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
                try {
                    linesRepository.getUiLine(navArgs.lineId, navArgs.lineType)
                        .also {
                            if (it == null) {
                                logError(
                                    "UI line (${navArgs.lineId}, ${navArgs.lineType}) not found"
                                )
                            }

                            // register a view for this line (assuming loadLine is called once)
                            historyDao.registerAccessed(true, navArgs.lineId, navArgs.lineType)
                        }
                } catch (e: Throwable) {
                    logError("Could not load UI line (${navArgs.lineId}, ${navArgs.lineType})", e)
                    null
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
                loading = true,
                error = false,
            )
        }

        viewModelScope.launch {
            var error = false
            val (tripsInDayCount, tripIndex, trip) = withContext(Dispatchers.IO) {
                try {
                    tripsRepository.getUiTrip(
                        lineId = navArgs.lineId,
                        lineType = navArgs.lineType,
                        referenceDateTime = referenceDateTime
                    )
                } catch (e: Throwable) {
                    logError(
                        "Could not load trip for UI line (${navArgs.lineId}, " +
                            "${navArgs.lineType}) at time $referenceDateTime",
                        e
                    )
                    error = true
                    Triple(0, 0, null)
                }
            }

            mutableUiState.update {
                it.copy(
                    tripsInDayCount = tripsInDayCount,
                    tripIndex = tripIndex,
                    trip = trip,
                    prevEnabled = tripIndex > 0,
                    nextEnabled = tripIndex < tripsInDayCount - 1,
                    referenceDateTime = referenceDateTime,
                    loading = false,
                    error = error,
                )
            }
        }
    }

    fun onReload() {
        val previousTrip = uiState.value.trip
        if (previousTrip == null) {
            // this could happen if an error happened while loading initial/more trips
            if (uiState.value.tripsInDayCount > 0) {
                // more trips failed loading, try to load again the currently set index
                loadIndex(uiState.value.tripIndex)
            } else {
                // initial trips failed loading, try to load again the current day
                setReferenceDateTime(uiState.value.referenceDateTime)
            }
            return
        }

        mutableUiState.update { it.copy(loading = true, error = false) }
        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                try {
                    tripsRepository.reloadUiTrip(
                        uiTrip = previousTrip,
                        index = uiState.value.tripIndex,
                        referenceDateTime = uiState.value.referenceDateTime
                    )
                } catch (e: Throwable) {
                    logError(
                        "Could not load trip ${previousTrip.tripId} for UI line " +
                                "(${navArgs.lineId}, ${navArgs.lineType})",
                        e
                    )
                    null
                }
            }

            if (trip == null) {
                mutableUiState.update { it.copy(loading = false, error = true) }
            } else {
                mutableUiState.update { it.copy(trip = trip, loading = false, error = false) }
            }
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
            mutableUiState.update { it.copy(loading = false, error = false) }
            return // this will happen when there are no trips in a day, for example
        }

        mutableUiState.update {
            it.copy(
                tripIndex = index,
                trip = null,
                prevEnabled = index > 0,
                nextEnabled = index < uiState.value.tripsInDayCount - 1,
                loading = true,
                error = false,
            )
        }

        viewModelScope.launch {
            val (trip, loadedFromNetwork) = withContext(Dispatchers.IO) {
                try {
                    tripsRepository.getUiTrip(
                        lineId = navArgs.lineId,
                        lineType = navArgs.lineType,
                        referenceDateTime = uiState.value.referenceDateTime,
                        index = index
                    )
                } catch (e: Throwable) {
                    logError(
                        "Could not load trip at index $index for UI line " +
                                "(${navArgs.lineId}, ${navArgs.lineType})",
                        e
                    )
                    Pair(null, true) // set loadedFromNetwork to true to prevent calling onReload
                }
            }

            // show the trip even if loadedFromNetwork is false (in which case it could be outdated)
            mutableUiState.update {
                it.copy(
                    trip = trip,
                    loading = false,
                    error = trip == null,
                )
            }

            if (!loadedFromNetwork) {
                // after showing the (possibly) outdated trip fast, reload it to show latest updates
                onReload()
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