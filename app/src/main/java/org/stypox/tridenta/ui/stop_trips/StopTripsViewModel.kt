package org.stypox.tridenta.ui.stop_trips

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
    private val tripsRepository: StopTripsRepository
) : AndroidViewModel(application) {

    private val navArgs = savedStateHandle.navArgs<StopTripsScreenNavArgs>()

    private val mutableUiState = MutableStateFlow(
        StopTripsUiState(
            stop = null,
            tripIndex = 0,
            trip = null,
            prevEnabled = false,
            nextEnabled = false,
            referenceDateTime = ZonedDateTime.now(),
            loading = true
        )
    )
    val uiState = mutableUiState.asStateFlow()
    
    private var tripsAtDateTimeList: StopTripsRepository.TripsAtDateTimeList? = null

    init {
        loadStop()
        setReferenceDateTime(ZonedDateTime.now())
    }

    private fun loadStop() {
        viewModelScope.launch {
            val stop = withContext(Dispatchers.IO) {
                stopsRepository.getDbStop(navArgs.stopId, navArgs.stopType)
            }
            mutableUiState.update { it.copy(stop = stop) }
        }
    }

    fun setReferenceDateTime(referenceDateTime: ZonedDateTime) {
        tripsAtDateTimeList = null
        mutableUiState.update {
            it.copy(
                tripIndex = 0,
                trip = null,
                prevEnabled = false,
                nextEnabled = false,
                referenceDateTime = referenceDateTime,
                loading = true
            )
        }

        viewModelScope.launch {
            tripsAtDateTimeList = withContext(Dispatchers.IO) {
                tripsRepository.getTrips(
                    stopId = navArgs.stopId,
                    stopType = navArgs.stopType,
                    referenceDateTime = referenceDateTime
                )
            }
            // load the first trip, which should be the next one arriving at the stop
            loadIndex(0)
        }
    }

    fun onReload() {
        val previousTrip = uiState.value.trip ?: return
        mutableUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                tripsRepository.reloadUiTrip(
                    uiTrip = previousTrip,
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
        if (index < 0 || index >= (tripsAtDateTimeList?.tripCount ?: -1)) {
            return // this should never happen, but just in case
        }

        mutableUiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                tripsAtDateTimeList?.getUiTripAtIndex(index)
            } ?: return@launch

            mutableUiState.update {
                it.copy(
                    loading = false,
                    tripIndex = index,
                    trip = trip,
                    prevEnabled = index > 0,
                    nextEnabled = index < (tripsAtDateTimeList?.tripCount ?: -1) - 1,
                )
            }
        }
    }
}