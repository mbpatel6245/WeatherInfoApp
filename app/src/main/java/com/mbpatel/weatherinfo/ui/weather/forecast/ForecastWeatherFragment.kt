package com.mbpatel.weatherinfo.ui.weather.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.databinding.FragmentForecastWeatherBinding
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.mbpatel.weatherinfo.ui.weather.WeatherInfoViewModel
import com.mbpatel.weatherinfo.utils.InjectorUtils
import com.mbpatel.weatherinfo.utils.convertDateToString
import com.mbpatel.weatherinfo.utils.convertToDate
import java.util.*
import kotlin.collections.HashMap

class ForecastWeatherFragment : Fragment() {
    private val args: ForecastWeatherFragmentArgs by navArgs()
    private val dataMapList = HashMap<Int, List<ForecastData>>()
    private val weatherViewModel: WeatherInfoViewModel by viewModels {
        InjectorUtils.provideWeatherViewModelFactory(
            this,
            LatLng(args.historyLatitude.toDouble(), args.historyLongitude.toDouble())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentForecastWeatherBinding>(
            inflater, R.layout.fragment_forecast_weather, container, false
        ).apply {
            viewModel = weatherViewModel
            lifecycleOwner = viewLifecycleOwner

            weatherViewModel.fetchForecastWeather.observe(viewLifecycleOwner) {
                val treeSet: MutableSet<Date> = TreeSet()
                for (item in it.forecastData)
                    treeSet.add(convertToDate(item.dtTxt!!))

                for ((i, item) in treeSet.withIndex()) {
                    val matchedList = ArrayList<ForecastData>()
                    for (sItem in it.forecastData) {
                        val date2 = convertToDate(sItem.dtTxt!!)
                        if (item == date2)
                            matchedList.add(sItem)
                    }
                    dataMapList[i] = matchedList
                }
                setAdapterData()
            }
        }
        return binding.root
    }

    private fun FragmentForecastWeatherBinding.setAdapterData() {
        for ((i, data) in dataMapList) {
            when (i) {
                0 -> {
                    txvDate1.text = convertDateToString(data[i].dtTxt)
                    subscribeUi(day1List, ForecastAdapter(weatherViewModel), data)
                }
                1 -> {
                    txvDate2.text = convertDateToString(data[i].dtTxt)
                    subscribeUi(day2List, ForecastAdapter(weatherViewModel), data)
                }
                2 -> {
                    txvDate3.text = convertDateToString(data[i].dtTxt)
                    subscribeUi(day3List, ForecastAdapter(weatherViewModel), data)
                }
                3 -> {
                    txvDate4.text = convertDateToString(data[i].dtTxt)
                    subscribeUi(day4List, ForecastAdapter(weatherViewModel), data)
                }
                4 -> {
                    txvDate5.text = convertDateToString(data[i].dtTxt)
                    subscribeUi(day5List, ForecastAdapter(weatherViewModel), data)
                }
//                5 -> {
//                    txvDate6.text = convertDateToString(data[i].dtTxt)
//                    subscribeUi(day6List, ForecastAdapter(cityViewModel), data)
//                }
            }
        }
    }

    private fun subscribeUi(
        list: RecyclerView,
        adapter: ForecastAdapter,
        data: List<ForecastData>
    ) {
        list.adapter = adapter
        adapter.submitList(data)
    }
    companion object {
        private const val WEATHER_POSITION = "INDEX"

        fun newInstance(counter: Int): ForecastWeatherFragment {
            val fragment = ForecastWeatherFragment()
            val args = Bundle()
            args.putInt(WEATHER_POSITION, counter)
            fragment.arguments = args
            return fragment
        }
    }
}