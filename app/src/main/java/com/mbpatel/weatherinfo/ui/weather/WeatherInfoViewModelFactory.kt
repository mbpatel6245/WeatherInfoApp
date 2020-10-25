package com.mbpatel.weatherinfo.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.api.WeatherRepository

class WeatherInfoViewModelFactory(
    private val repository: WeatherRepository,
    private val mLatLng: LatLng
) : ViewModelProvider.Factory  {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vModel:ViewModel= WeatherInfoViewModel(repository,mLatLng) as T
        return vModel as T
    }
}