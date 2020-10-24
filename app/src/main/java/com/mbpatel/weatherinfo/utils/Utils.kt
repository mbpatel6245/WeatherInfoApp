package com.mbpatel.weatherinfo.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

/**
 * Display the Toast
 * @param context context
 * @param message string message
 */
fun showToast(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}
fun convertToTime(item: String?): String {
    item?.let {
        val df_input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val df_output = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val parsed = df_input.parse(item)
            return df_output.format(parsed)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ""
}
fun convertDateToString(item: String?): String {
    item?.let {
        val df_input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val df_output = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

        try {
            val parsed = df_input.parse(item)
            return df_output.format(parsed)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ""
}
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso.get()
        .load("http://openweathermap.org/img/w/$imageUrl.png")
        .into(view)
}
fun convertToDate(item: String) :Date{
    val df_input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return df_input.parse(item)
}