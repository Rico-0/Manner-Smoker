package com.kapstone.mannersmoker.model.data.post

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class PostSendModel(
    @SerializedName("content")
    @Expose
    var content : String,

    @SerializedName("title")
    @Expose
    var title : String,

    @SerializedName("userId")
    @Expose
    var userId : Int
)
