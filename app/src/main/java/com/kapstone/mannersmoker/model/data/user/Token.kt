package com.kapstone.mannersmoker.model.data.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kapstone.mannersmoker.model.data.smoke.Smoke

data class Token(
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val token : TokenValue
)
