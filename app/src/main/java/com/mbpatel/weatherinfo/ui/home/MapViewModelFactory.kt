package com.mbpatel.weatherinfo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mbpatel.weatherinfo.db.HistoryRepository

class MapViewModelFactory(
    private val repository: HistoryRepository

) : ViewModelProvider.Factory  {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vModel:ViewModel= MapViewModel(repository) as T
        // (vModel as AddProductsViewModel).itemCode=itemCode
        return vModel as T
    }
}