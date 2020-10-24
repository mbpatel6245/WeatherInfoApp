package com.mbpatel.weatherinfo.api

import androidx.lifecycle.MutableLiveData
import com.mbpatel.weatherinfo.BuildConfig
import com.mbpatel.weatherinfo.utils.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository {
    private val newsApi: ApiGenerator = RetrofitService.create()

    fun getTodayWeather(mLat: Double, mLog: Double): MutableLiveData<TodayWeatherResponse> {
        val newsData: MutableLiveData<TodayWeatherResponse> =
            MutableLiveData<TodayWeatherResponse>()
        newsApi.getTodayWeather(mLat, mLog, BuildConfig.APP_ID, Config.weatherUnits)
            .enqueue(object :
                Callback<TodayWeatherResponse?> {
                override fun onResponse(
                    call: Call<TodayWeatherResponse?>?,
                    response: Response<TodayWeatherResponse?>
                ) {
                    if (response.isSuccessful) {
                        newsData.setValue(response.body())
                    }
                }

                override fun onFailure(call: Call<TodayWeatherResponse?>?, t: Throwable?) {
                    t!!.printStackTrace()
                    //newsData.setValue(null)
                }
            })
        return newsData
    }

    fun getForecastWeather(mLat: Double, mLog: Double): MutableLiveData<ForecastWeatherResponse> {
        val newsData: MutableLiveData<ForecastWeatherResponse> =
            MutableLiveData<ForecastWeatherResponse>()
        newsApi.getForecastWeather(mLat, mLog, BuildConfig.APP_ID, Config.weatherUnits)
            .enqueue(object :
                Callback<ForecastWeatherResponse?> {
                override fun onResponse(
                    call: Call<ForecastWeatherResponse?>?,
                    response: Response<ForecastWeatherResponse?>
                ) {
                    if (response.isSuccessful) {
                        newsData.setValue(response.body())
                    }
                }

                override fun onFailure(call: Call<ForecastWeatherResponse?>?, t: Throwable?) {
                    t!!.printStackTrace()
//                    newsData.setValue(null)
                }
            })
        return newsData
    }

    companion object {
        @Volatile
        private var weatherRepository: WeatherRepository? = null

        fun getInstance(): WeatherRepository {
            if (weatherRepository == null) {
                weatherRepository = WeatherRepository()
            }
            return weatherRepository!!
        }

    }

}