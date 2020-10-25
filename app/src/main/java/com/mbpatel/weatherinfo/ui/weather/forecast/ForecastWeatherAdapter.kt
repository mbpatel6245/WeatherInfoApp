package com.mbpatel.weatherinfo.ui.weather.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.databinding.RowForecastListBinding
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.mbpatel.weatherinfo.ui.weather.WeatherInfoViewModel
import com.mbpatel.weatherinfo.utils.convertToTime
import com.mbpatel.weatherinfo.utils.loadImage

/**
 * Adapter for the [RecyclerView] in [ForecastWeatherFragment].
 */
class ForecastAdapter(private val viewModel: WeatherInfoViewModel) :
    ListAdapter<ForecastData, ForecastAdapter.ForecastViewHolder>(WeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_forecast_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }

    class ForecastViewHolder(private val binding: RowForecastListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ForecastData,
            cityViewModel: WeatherInfoViewModel
        ) {
            with(binding) {
                dataItem = item
                viewModel = cityViewModel
                executePendingBindings()
                loadImage(weatherIcon, item.weather!![0].icon)
                txvTime.text = convertToTime(item.dtTxt)
            }
        }
    }
}

private class WeatherDiffCallback : DiffUtil.ItemCallback<ForecastData>() {

    override fun areItemsTheSame(oldItem: ForecastData, newItem: ForecastData): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: ForecastData, newItem: ForecastData): Boolean {
        return oldItem.dtTxt == newItem.dtTxt
    }
}