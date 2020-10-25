package com.mbpatel.weatherinfo.ui.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.ui.weather.forecast.ForecastWeatherFragment
import com.mbpatel.weatherinfo.ui.weather.today.TodayWeatherFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, latLng: LatLng) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return if(position==0)
            TodayWeatherFragment.newInstance(position)
        else
            ForecastWeatherFragment.newInstance(position)
    }

    override fun getItemCount(): Int {
        return 2
    }
}