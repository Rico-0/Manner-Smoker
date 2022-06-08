package com.kapstone.mannersmoker.model.data.reply

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReplyGetModel (
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val replyData : Reply
    )