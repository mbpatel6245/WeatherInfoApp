package com.mbpatel.weatherinfo.ui.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.databinding.FragmentWeatherinfoBinding
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.mbpatel.weatherinfo.ui.history.HistoryFragmentDirections
import com.mbpatel.weatherinfo.utils.InjectorUtils
import kotlin.collections.HashMap

class WeatherInfoFragment : Fragment() {
    private val args: WeatherInfoFragmentArgs by navArgs()
//    private val dataMapList = HashMap<Int, List<ForecastData>>()
//    private val weatherViewModel: WeatherInfoViewModel by viewModels {
//        InjectorUtils.provideWeatherViewModelFactory(
//            this,
//            LatLng(args.historyLatitude.toDouble(), args.historyLongitude.toDouble())
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentWeatherinfoBinding>(
            inflater, R.layout.fragment_weatherinfo, container, false
        ).apply {
            tabViewPager.adapter =
                ViewPagerAdapter(
                    requireActivity(),LatLng(args.historyLatitude.toDouble(), args.historyLongitude.toDouble())
                )
           /* tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.e("TAB SELECTED","TAB SELECTED")

                    val direction =
                        WeatherInfoFragmentDirections.actionWeatherFragmentToNestedGraph(
                            args.historyLatitude,args.historyLongitude
                        )
                    findNavController().navigate(direction)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })*/
            TabLayoutMediator(tabLayout, tabViewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.title_current_weather)
                       // tab.icon = drawable1

                    }
                    1 -> {
                        tab.text = getString(R.string.title_forecast_weather)
                        //tab.icon = drawable2
                    }

                }
            }.attach()
        }
        return binding.root
    }


}