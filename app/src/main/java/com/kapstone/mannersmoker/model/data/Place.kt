package com.kapstone.mannersmoker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("area")
    @Expose
    val areaCode : Int,

    @SerializedName("latitude")
    @Expose
    val latitude : Double,

    @SerializedName("longitude")
    @Expose
    val longtitude : Double
)
