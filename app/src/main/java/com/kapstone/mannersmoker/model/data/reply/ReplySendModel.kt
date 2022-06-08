package com.kapstone.mannersmoker.model.data.reply

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class ReplySendModel (
    @SerializedName("postId")
    @Expose
    val postId : Int,

    @SerializedName("replyContent")
    @Expose
    val replyContent : String,

    @SerializedName("userId")
    @Expose
    val userId : Int
)
