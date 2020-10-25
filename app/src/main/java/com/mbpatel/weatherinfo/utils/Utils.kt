package com.mbpatel.weatherinfo.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.model.forecast.ForecastData
import com.mbpatel.weatherinfo.ui.login.LoginActivity
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

fun convertToDate(item: String): Date {
    val df_input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return df_input.parse(item)
}

fun setAlertDialog(mContext: Activity) {
    AlertDialog.Builder(mContext)
        .setTitle(R.string.menu_logout)
        .setMessage(R.string.prompt_logout)
        .setPositiveButton(
            R.string.title_yes
        ) { _, _ ->
            mContext.getSharedPreferences(Constants.PREFERENCE_KEY, AppCompatActivity.MODE_PRIVATE)
                .edit()
                .clear().apply()
            mContext.startActivity(Intent(mContext, LoginActivity::class.java))
            mContext.finish()
        }
        .setNegativeButton(R.string.title_no, null)
        .show()
}