package com.mbpatel.weatherinfo.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiGenerator {
    /**
     * Get Today Weather Data
     */
    @GET("data/2.5/weather")
    fun getTodayWeather(
        @Query("lat") mLat: Double, @Query("lon") mLon: Double,
        @Query("appid") mAppId: String, @Query("units") mUnits: String
    ): Call<TodayWeatherResponse>

    /**
     * Get Forecast Weather Data
     */
    @GET("data/2.5/forecast")
    fun getForecastWeather(
        @Query("lat") mLat: Double, @Query("lon") mLon: Double,
        @Query("appid") mAppId: String, @Query("units") mUnits: String
    ): Call<ForecastWeatherResponse>
}