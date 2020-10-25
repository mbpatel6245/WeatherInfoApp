package com.mbpatel.weatherinfo.ui.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.ui.weather.forecast.ForecastWeatherFragment
import com.mbpatel.weatherinfo.ui.weather.today.TodayWeatherFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity,var latLng: LatLng) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return if(position==0)
            TodayWeatherFragment.newInstance(position,latLng)
        else
            ForecastWeatherFragment.newInstance(position,latLng)
    }

    override fun getItemCount(): Int {
        return 2
    }
}