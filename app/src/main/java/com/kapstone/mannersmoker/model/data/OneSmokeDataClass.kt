package com.kapstone.mannersmoker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

// 흡연할 때마다 DB에 하나씩 넣음 (userId)
// 받아올 때는 날짜로만 받아옴
data class OneSmokeDataClass (
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : Int,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val SmokeData : List<Smoke>
)
