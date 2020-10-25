package com.mbpatel.weatherinfo.utils

import android.content.Context
import android.content.SharedPreferences

private fun initPreference(mContext: Context): SharedPreferences {
    return mContext.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
}

fun savePreference(mContext: Context, mKey: String, mValue: String?) {
    mValue?.let {
        initPreference(mContext).edit().putString(mKey, mValue).apply()
    }
}

fun getPreference(mContext: Context, mKey: String): String {
    return initPreference(mContext).getString(mKey, "").toString()
}