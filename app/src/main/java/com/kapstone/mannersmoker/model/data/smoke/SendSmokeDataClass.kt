package com.kapstone.mannersmoker.model.data.smoke

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendSmokeDataClass(
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val SmokeData : Smoke
)
