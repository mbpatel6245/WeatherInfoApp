package com.mbpatel.weatherinfo.model.today

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso


class Weather {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("main")
    @Expose
    var main: String = ""

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("icon")
    @Expose
    var icon: String = ""

}