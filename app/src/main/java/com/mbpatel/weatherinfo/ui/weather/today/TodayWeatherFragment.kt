package com.mbpatel.weatherinfo.ui.weather.today

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
import com.mbpatel.weatherinfo.databinding.FragmentTodayWeatherBinding
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.mbpatel.weatherinfo.ui.weather.WeatherInfoViewModel
import com.mbpatel.weatherinfo.utils.InjectorUtils
import com.mbpatel.weatherinfo.utils.convertToDate
import com.mbpatel.weatherinfo.utils.loadImage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TodayWeatherFragment : Fragment() {
    private val args: TodayWeatherFragmentArgs by navArgs()
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
        val binding = DataBindingUtil.inflate<FragmentTodayWeatherBinding>(
            inflater, R.layout.fragment_today_weather, container, false
        ).apply {
            viewModel = weatherViewModel
            lifecycleOwner = viewLifecycleOwner

            val c: Date = Calendar.getInstance().time

            val df = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            txvDate.text = df.format(c)
            weatherViewModel.fetchTodayWeather.observe(
                viewLifecycleOwner, {
                    it?.let {
                        loadImage(weatherIcon, it.weather!![0].icon)
                    }
                })

            weatherViewModel.fetchForecastWeather.observe(viewLifecycleOwner) {
                it?.let {
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


        }
        return binding.root
    }

    private fun FragmentTodayWeatherBinding.setAdapterData() {
        // txvDate1.text = convertDateToString(data[i].dtTxt)
        if (dataMapList.size > 0)
            dataMapList[0]?.let {
                subscribeUi(day1List, TodayWeatherAdapter(weatherViewModel), it.toList())
            }
    }

    private fun subscribeUi(
        list: RecyclerView,
        adapter: TodayWeatherAdapter,
        data: List<ForecastData>
    ) {
        list.adapter = adapter
        adapter.submitList(data)
    }

    companion object {
        private const val WEATHER_POSITION = "INDEX"

        fun newInstance(counter: Int): TodayWeatherFragment {
            val fragment = TodayWeatherFragment()
            val args = Bundle()
            args.putInt(WEATHER_POSITION, counter)
            fragment.arguments = args
            return fragment
        }
    }
}