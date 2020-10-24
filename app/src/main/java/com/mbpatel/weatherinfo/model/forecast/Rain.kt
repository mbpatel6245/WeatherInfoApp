package com.mbpatel.weatherinfo.model.forecast

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Rain {
    @SerializedName("3h")
    @Expose
    var _3h: Double? = null
}