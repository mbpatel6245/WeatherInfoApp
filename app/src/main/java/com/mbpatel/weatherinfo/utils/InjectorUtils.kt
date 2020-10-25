package com.mbpatel.weatherinfo.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.api.WeatherRepository
import com.mbpatel.weatherinfo.db.AppDatabase
import com.mbpatel.weatherinfo.db.HistoryRepository
import com.mbpatel.weatherinfo.ui.history.HistoryViewModelFactory
import com.mbpatel.weatherinfo.ui.home.MapViewModelFactory
import com.mbpatel.weatherinfo.ui.weather.WeatherInfoViewModelFactory


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getWeatherRepository(context: Context): WeatherRepository {
        return WeatherRepository.getInstance()
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).historyDao()
        )
    }
    fun provideWeatherViewModelFactory(fragment: Fragment, mLatLng: LatLng): WeatherInfoViewModelFactory {
        return WeatherInfoViewModelFactory(
            getWeatherRepository(fragment.requireContext()), mLatLng
        )
    }
    fun provideMapViewModelFactory(fragment: Fragment): MapViewModelFactory {
        return MapViewModelFactory(getHistoryRepository(fragment.requireContext()))
    }

    fun provideHomeListViewModelFactory(fragment: Fragment): HistoryViewModelFactory {
        return HistoryViewModelFactory(
            getHistoryRepository(fragment.requireContext()),
            fragment
        )
    }

}