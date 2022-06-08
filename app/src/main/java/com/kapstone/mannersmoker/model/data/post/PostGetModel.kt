package com.kapstone.mannersmoker.model.data.post

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostGetModel(
    @SerializedName("httpStatus")
    @Expose
    val httpStatus : String,

    @SerializedName("message")
    @Expose
    val message : String,

    @SerializedName("response")
    @Expose
    val postData : Post
)
