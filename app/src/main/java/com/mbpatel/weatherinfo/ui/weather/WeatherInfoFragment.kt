package com.mbpatel.weatherinfo.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mbpatel.weatherinfo.R

class WeatherInfoFragment : Fragment() {

    private lateinit var weatherInfoViewModel: WeatherInfoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        weatherInfoViewModel =
                ViewModelProvider(this).get(WeatherInfoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_weatherinfo, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        weatherInfoViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}