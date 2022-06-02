package com.kapstone.mannersmoker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class Smoke(
    @SerializedName("createDate")
    @Expose
    val createDate : Date,

    @SerializedName("day")
    @Expose
    val day : Int,

    @SerializedName("month")
    @Expose
    val month : Int,

    @SerializedName("year")
    @Expose
    val year : Int,

    // 이거 Int인지 String인지 모르겠음
    @SerializedName("userId")
    @Expose
    val userId : String,
)
