package org.stypox.tridenta.ui.stop_trips

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
import org.stypox.tridenta.repo.StopTripsRepository
import org.stypox.tridenta.repo.StopsRepository
import org.stypox.tridenta.ui.navArgs
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class StopTripsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    application: Application,
    private val stopsRepository: StopsRepository,
    private val tripsRepository: StopTripsRepository,
    private val historyDao: HistoryDao
) : AndroidViewModel(application) {

    private val navArgs = savedStateHandle.navArgs<StopTripsScreenNavArgs>()

    private val mutableUiState = MutableStateFlow(
        StopTripsUiState(
            stop = null,
            tripIndex = 0,
            trip = null,
            prevEnabled = false,
            nextEnabled = false,
            referenceDateTime = ZonedDateTime.now().withZoneSameInstant(ROME_ZONE_ID),
            loading = true,
            error = false,
        )
    )
    val uiState = mutableUiState.asStateFlow()
    
    private var tripsAtDateTimeList: StopTripsRepository.TripsAtDateTimeList? = null
    private var tripReloadJob: Job? = null

    val isFavorite = historyDao.isFavorite(
        isLine = false,
        id = navArgs.stopId,
        type = navArgs.stopType
    )

    init {
        loadStop()
        setReferenceDateTime(ZonedDateTime.now())
    }


    fun setReferenceDateTime(referenceDateTimeCurrentZone: ZonedDateTime) {
        cancelTripReloadJobAndLaunch {
            setReferenceDateTimeAsync(referenceDateTimeCurrentZone)
        }
    }

    fun onReload() {
        cancelTripReloadJobAndLaunch {
            onReloadAsync()
        }
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
                    isLine = false,
                    id = navArgs.stopId,
                    type = navArgs.stopType,
                    isFavorite = isFavorite.value?.not() ?: true
                )
            }
        }
    }


    private fun loadStop() {
        // this job is independent from trip reloading jobs, so don't use cancelReloadJobAndLaunch
        viewModelScope.launch {
            val stop = withContext(Dispatchers.IO) {
                try {
                    stopsRepository.getDbStop(navArgs.stopId, navArgs.stopType).also {
                        if (it == null) {
                            logError(
                                "DB stop (${navArgs.stopId}, ${navArgs.stopType}) not found"
                            )
                        }

                        // register a view for this stop (assuming loadStop is called once)
                        historyDao.registerAccessed(false, navArgs.stopId, navArgs.stopType)
                    }
                } catch (e: Throwable) {
                    logError("Could not load DB stop (${navArgs.stopId}, ${navArgs.stopType})", e)
                    null
                }
            }
            mutableUiState.update { it.copy(stop = stop) }
        }
    }

    private fun cancelTripReloadJobAndLaunch(tripLoadingFunction: suspend () -> Unit) {
        tripReloadJob?.cancel()
        tripReloadJob = viewModelScope.launch {
            // clear errors and set the state to "loading" before starting to load
            mutableUiState.update { it.copy(loading = true, error = false) }
            tripLoadingFunction()
            // set "loading" to false when loading finished (errors are set inside the function)
            mutableUiState.update { it.copy(loading = false) }
        }
    }

    private suspend fun setReferenceDateTimeAsync(referenceDateTimeCurrentZone: ZonedDateTime) {
        val referenceDateTime = referenceDateTimeCurrentZone.withZoneSameInstant(ROME_ZONE_ID)
        tripsAtDateTimeList = null
        mutableUiState.update {
            it.copy(
                tripIndex = 0,
                trip = null,
                prevEnabled = false,
                nextEnabled = false,
                referenceDateTime = referenceDateTime,
            )
        }

        tripsAtDateTimeList = withContext(Dispatchers.IO) {
            try {
                tripsRepository.getTrips(
                    stopId = navArgs.stopId,
                    stopType = navArgs.stopType,
                    referenceDateTime = referenceDateTime
                )
            } catch (e: Throwable) {
                logError(
                    "Could not load trips for DB stop (${navArgs.stopId}, " +
                            "${navArgs.stopType}) at time $referenceDateTime",
                    e
                )
                null
            }
        }

        if (tripsAtDateTimeList == null) {
            mutableUiState.update { it.copy(error = true) }
        } else {
            // show the first trip, which should be the next one arriving at the stop;
            // requestedByUser is false since the trip is surely up-to-date, as it was just fetched
            loadIndexAsync(0, false)
        }
    }

    private suspend fun onReloadAsync() {
        val previousTrip = uiState.value.trip
        if (previousTrip == null) {
            // initial trips failed loading, try to load again the current day
            setReferenceDateTimeAsync(uiState.value.referenceDateTime)
            return
        }

        val trip = withContext(Dispatchers.IO) {
            try {
                tripsAtDateTimeList?.reloadUiTrip(
                    uiTrip = previousTrip,
                    index = uiState.value.tripIndex,
                    referenceDateTime = uiState.value.referenceDateTime
                )
            } catch (e: Throwable) {
                logError(
                    "Could not load trip ${previousTrip.tripId} for DB stop " +
                            "(${navArgs.stopId}, ${navArgs.stopType})",
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
        if (index >= 0 && index < (tripsAtDateTimeList?.tripCount ?: -1)) {
            // only cancel any currently running job if there is something to do; the above
            // condition will be false e.g. when there are no trips in a day (but not only for that)
            cancelTripReloadJobAndLaunch {
                loadIndexAsync(index, true)
            }
        }
    }

    private suspend fun loadIndexAsync(index: Int, requestedByUser: Boolean) {
        mutableUiState.update {
            it.copy(
                tripIndex = index,
                trip = null,
                prevEnabled = index > 0,
                nextEnabled = index < (tripsAtDateTimeList?.tripCount ?: -1) - 1,
            )
        }

        val trip = withContext(Dispatchers.IO) {
            try {
                tripsAtDateTimeList?.getUiTripAtIndex(index)
            } catch (e: Throwable) {
                logError(
                    "Could not load trip at index $index for DB stop " +
                            "(${navArgs.stopId}, ${navArgs.stopType})",
                    e
                )
                null
            }
        }

        mutableUiState.update {
            it.copy(
                trip = trip,
                loading = false,
                error = trip == null,
            )
        }

        if (requestedByUser && trip != null && trip.completedStops < trip.stopTimes.size) {
            // after showing the (possibly) outdated trip fast, reload it to show latest updates
            // (but reload it only if there actually is a trip and it is not completed)
            onReloadAsync()
        }
    }
}