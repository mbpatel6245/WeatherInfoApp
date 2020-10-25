package com.mbpatel.weatherinfo.ui.weather

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.api.WeatherRepository

class WeatherInfoViewModel(private val repository: WeatherRepository, location: LatLng) :
    ViewModel() {

    var fetchTodayWeather = repository.getTodayWeather(
        location.latitude,
        location.longitude
    )
    var fetchForecastWeather = repository.getForecastWeather(
        location.latitude,
        location.longitude
    )

    /*private var todayWeatherData = MutableLiveData<LatLng>()*/

  /*  var getTodayWeatherData: LiveData<TodayWeatherResponse> =
        Transformations.switchMap(todayWeatherData) { mLatLng: LatLng ->
            repository.getTodayWeather(mLatLng.latitude, mLatLng.longitude)
        }

    fun setBookMarkLocation(mLatLng: LatLng) {
        this.todayWeatherData.value = mLatLng
    }*/

}