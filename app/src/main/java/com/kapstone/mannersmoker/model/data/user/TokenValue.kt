package com.kapstone.mannersmoker.model.data.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenValue(
    @SerializedName("accessToken")
    @Expose
    val accessToken : String,

    @SerializedName("refreshToken")
    @Expose
    val refreshToken : String
)
