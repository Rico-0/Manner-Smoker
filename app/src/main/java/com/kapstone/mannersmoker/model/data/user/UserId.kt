package com.kapstone.mannersmoker.model.data.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserId(
    @SerializedName("userId")
    @Expose
    val userId : Int
)
