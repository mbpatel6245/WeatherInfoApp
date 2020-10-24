package com.mbpatel.weatherinfo.api

import com.google.gson.annotations.SerializedName
import com.mbpatel.weatherinfo.model.forecast.City
import com.mbpatel.weatherinfo.model.forecast.ForecastData

class ForecastWeatherResponse {
    @SerializedName("cod")
    var cod: String? = null

    @SerializedName("message")
    var message: Int? = null

    @SerializedName("cnt")
    var cnt: Int? = null

    @SerializedName("list")
    var forecastData: List<ForecastData> = ArrayList()

    @SerializedName("city")
    var city: City? = null
}