package com.kapstone.mannersmoker.model.data.smoke

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kapstone.mannersmoker.model.data.smoke.Smoke

// 흡연할 때마다 DB에 하나씩 넣음 (userId)
// 받아올 때는 날짜로만 받아옴
data class SmokeDataClass (
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val SmokeData : List<Smoke>
)
