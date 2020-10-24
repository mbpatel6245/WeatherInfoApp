package com.mbpatel.weatherinfo.model.today

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Wind {
    @SerializedName("speed")
    @Expose
    var speed: Double = 0.0

    @SerializedName("deg")
    @Expose
    var deg: Int = 0
}