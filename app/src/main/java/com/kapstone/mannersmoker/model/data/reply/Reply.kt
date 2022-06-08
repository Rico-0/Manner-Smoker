package com.kapstone.mannersmoker.model.data.reply

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*

data class Reply(
    @SerializedName("createdDate")
    @Expose
    val createdDate : String,

    @SerializedName("modifiedDate")
    @Expose
    val modifiedDate : String,

    @SerializedName("postId")
    @Expose
    val postId : Int,

    @SerializedName("replyContent")
    @Expose
    val replyContent : String,

    @SerializedName("userId")
    @Expose
    val userId : Int,

    @SerializedName("replyId")
    @Expose
    val replyId : Int
)
