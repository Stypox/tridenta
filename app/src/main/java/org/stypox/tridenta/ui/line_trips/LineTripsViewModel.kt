package org.stypox.tridenta.ui.line_trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.stypox.tridenta.db.HistoryDao
import org.stypox.tridenta.extractor.ROME_ZONE_ID
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
            referenceDateTime = ZonedDateTime.now().withZoneSameInstant(ROME_ZONE_ID),
            stopIdToHighlight = navArgs.stopIdToHighlight,
            stopTypeToHighlight = navArgs.stopTypeToHighlight,
            loading = true,
            error = false,
        )
    )
    val uiState = mutableUiState.asStateFlow()

    private var tripReloadJob: Job? = null

    val isFavorite = historyDao.isFavorite(
        isLine = true,
        id = navArgs.lineId,
        type = navArgs.lineType
    )

    init {
        loadLine()
        setReferenceDateTime(ZonedDateTime.now())
    }

    fun setReferenceDateTime(referenceDateTimeCurrentZone: ZonedDateTime) {
        cancelTripReloadJobAndLaunch {
            setReferenceDateTimeAsync(referenceDateTimeCurrentZone)
        }
    }

    fun onReload() {
        cancelTripReloadJobAndLaunch(this::onReloadAsync)
    }

    fun onPrevClicked() {
        loadIndex(uiState.value.tripIndex - 1)
    }

    fun onNextClicked() {
        loadIndex(uiState.value.tripIndex + 1)
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


    private fun loadLine() {
        // this job is independent from trip reloading jobs, so don't use cancelReloadJobAndLaunch
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

    private fun cancelTripReloadJobAndLaunch(tripLoadingFunction: suspend () -> Unit) {
        tripReloadJob?.cancel()
        tripReloadJob = viewModelScope.launch {
            // clear errors and set the state to "loading" before starting to load
            mutableUiState.update { it.copy(loading = true, error = false) }
            tripLoadingFunction()
            // set "loading" to false when loading finishes (errors are set inside the function)
            mutableUiState.update { it.copy(loading = false) }
        }
    }

    private suspend fun setReferenceDateTimeAsync(referenceDateTimeCurrentZone: ZonedDateTime) {
        val referenceDateTime = referenceDateTimeCurrentZone.withZoneSameInstant(ROME_ZONE_ID)
        mutableUiState.update {
            it.copy(
                tripsInDayCount = 0,
                tripIndex = 0,
                trip = null,
                prevEnabled = false,
                nextEnabled = false,
                referenceDateTime = referenceDateTime,
            )
        }

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
                error = error,
            )
        }
    }

    private suspend fun onReloadAsync() {
        val previousTrip = uiState.value.trip
        if (previousTrip == null) {
            // this could happen if an error happened while loading initial/more trips
            if (uiState.value.tripsInDayCount > 0) {
                // more trips failed loading, try to load again the currently set index
                loadIndexAsync(uiState.value.tripIndex)
            } else {
                // initial trips failed loading, try to load again the current day
                setReferenceDateTimeAsync(uiState.value.referenceDateTime)
            }
            return
        }

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
            // keep previous trip intact, we don't want to hide information that we do have!
            mutableUiState.update { it.copy(error = true) }
        } else {
            mutableUiState.update { it.copy(trip = trip) }
        }
    }

    private fun loadIndex(index: Int) {
        if (index >= 0 && index < uiState.value.tripsInDayCount) {
            // only cancel any currently running job if there is something to do; the above
            // condition will be false e.g. when there are no trips in a day (but not only for that)
            cancelTripReloadJobAndLaunch {
                loadIndexAsync(index)
            }
        }
    }

    private suspend fun loadIndexAsync(index: Int) {
        mutableUiState.update {
            it.copy(
                tripIndex = index,
                trip = null,
                prevEnabled = index > 0,
                nextEnabled = index < uiState.value.tripsInDayCount - 1,
            )
        }

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
                Pair(null, true /* <- useless when trip == null */)
            }
        }

        // show the trip even if loadedFromNetwork is false (in which case it could be outdated)
        mutableUiState.update {
            it.copy(
                trip = trip,
                error = trip == null,
            )
        }

        if (!loadedFromNetwork && trip != null && trip.completedStops < trip.stopTimes.size) {
            // after showing the (possibly) outdated trip fast, reload it to show latest updates
            // (but reload it only if there actually is a trip and it is not completed)
            onReloadAsync()
        }
    }
}