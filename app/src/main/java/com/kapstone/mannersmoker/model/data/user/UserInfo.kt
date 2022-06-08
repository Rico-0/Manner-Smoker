package com.kapstone.mannersmoker.model.data.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class UserInfo(
    @SerializedName("email")
    @Expose
    val email : String,

    @SerializedName("id")
    @Expose
    val userId : Int,

    @SerializedName("username")
    @Expose
    val username : String
)
