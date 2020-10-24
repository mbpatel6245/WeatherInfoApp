package com.mbpatel.weatherinfo.ui.history

import com.mbpatel.weatherinfo.db.History
import com.mbpatel.weatherinfo.db.HistoryRepository

import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * The ViewModel for [HistoryFragment].
 */
class HistoryViewModel internal constructor(
    private val repository: HistoryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var searchableHistory = MutableLiveData<String>()

    fun deleteHistory(mHistory: History) {
        viewModelScope.launch {
            repository.deleteHistory(mHistory)
        }
    }

    var history: LiveData<List<History>> =
        Transformations.switchMap(searchableHistory) { code: String ->
            if (code.isEmpty())
                repository.fetchAllHistory()
            else
                repository.searchHistory(code)
        }

    fun setSearchableHistory(name: String) {
        this.searchableHistory.value = name
    }

}