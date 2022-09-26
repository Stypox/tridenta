package org.stypox.tridenta.ui.trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.repo.LineTripsRepository
import org.stypox.tridenta.repo.data.UiLine
import java.time.ZonedDateTime

class LineTripsViewModel @AssistedInject constructor(
    application: Application,
    private val tripsRepository: LineTripsRepository,
    @Assisted private val line: UiLine
) : AndroidViewModel(application) {

    private val mutableUiState = MutableStateFlow(
        LineTripsUiState(
            tripsInDayCount = 0,
            tripIndex = 0,
            trip = null,
            prevEnabled = false,
            nextEnabled = false,
            referenceDateTime = ZonedDateTime.now(),
            loading = true
        )
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        setReferenceDateTime(ZonedDateTime.now())
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
                    lineId = line.lineId,
                    lineType = line.type,
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

    fun onPrevClicked() {
        loadIndex(uiState.value.tripIndex - 1)
    }

    fun onNextClicked() {
        loadIndex(uiState.value.tripIndex + 1)
    }

    fun loadIndex(index: Int) {
        if (index < 0 || index >= uiState.value.tripsInDayCount) {
            return // this should never happen, but just in case
        }

        mutableUiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val trip = withContext(Dispatchers.IO) {
                tripsRepository.getUiTrip(
                    lineId = line.lineId,
                    lineType = line.type,
                    referenceDateTime = uiState.value.referenceDateTime,
                    index = index
                )
            }

            mutableUiState.update {
                it.copy(
                    tripIndex = index,
                    trip = trip,
                    prevEnabled = index > 0,
                    nextEnabled = index < uiState.value.tripsInDayCount - 1,
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(line: UiLine): LineTripsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(factory: Factory, line: UiLine):
                ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(line) as T
            }
        }
    }
}