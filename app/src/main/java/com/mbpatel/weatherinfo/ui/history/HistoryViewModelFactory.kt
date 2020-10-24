package com.mbpatel.weatherinfo.ui.history

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.mbpatel.weatherinfo.db.HistoryRepository

/**
 * Factory for creating a [HistoryViewModel] with a constructor that takes a [HistoryRepository].
 */
class HistoryViewModelFactory(
    private val repository: HistoryRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return HistoryViewModel(repository, handle) as T
    }
}
