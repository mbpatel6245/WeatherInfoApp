package com.mbpatel.weatherinfo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbpatel.weatherinfo.db.HistoryRepository
import kotlinx.coroutines.launch

class MapViewModel(var historyRepository: HistoryRepository) :
    ViewModel() {

    /** Fetch the history data */
    var fetchAllHistory = historyRepository.fetchAllHistory()

    /**
     * Use for the save/edit comment data
     */
    fun addHistory(
        bLat: Double,
        bLng: Double,
        bName: String
    ) {
        viewModelScope.launch {
            historyRepository.saveHistory(bLat, bLng, bName)
        }
    }
}