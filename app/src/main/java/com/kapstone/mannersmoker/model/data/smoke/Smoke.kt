package com.kapstone.mannersmoker.model.data.smoke

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*

data class Smoke(
    @SerializedName("createdDate")
    @Expose
    val createDate : String,

    @SerializedName("day")
    @Expose
    val day : Int,

    @SerializedName("month")
    @Expose
    val month : Int,

    @SerializedName("year")
    @Expose
    val year : Int,

    @SerializedName("userId")
    @Expose
    val userId : Int,
)
