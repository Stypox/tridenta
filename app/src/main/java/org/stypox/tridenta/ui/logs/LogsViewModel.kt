package org.stypox.tridenta.ui.logs

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.stypox.tridenta.db.LogDao
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(logDao: LogDao) : ViewModel() {
    val logs = logDao.getLogs()
}