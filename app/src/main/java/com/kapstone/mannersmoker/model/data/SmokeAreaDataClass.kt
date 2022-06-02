package com.kapstone.mannersmoker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SmokeAreaDataClass (
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val placeData : List<Place>
)
