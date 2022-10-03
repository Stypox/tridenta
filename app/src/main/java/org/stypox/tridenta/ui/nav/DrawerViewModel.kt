package org.stypox.tridenta.ui.nav

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.stypox.tridenta.repo.HistoryRepository
import javax.inject.Inject


@HiltViewModel
class DrawerViewModel @Inject constructor(
    application: Application,
    historyRepository: HistoryRepository
) : AndroidViewModel(application) {
    val favorites = historyRepository.getFavorites(viewModelScope)
    val history = historyRepository.getHistory(viewModelScope)
}