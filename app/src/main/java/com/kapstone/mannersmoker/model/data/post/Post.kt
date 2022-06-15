package com.kapstone.mannersmoker.model.data.post

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*

data class Post (
    @SerializedName("content")
    @Expose
    var content : String,

    @SerializedName("createdDate")
    @Expose
    var createdDate : String,

    @SerializedName("modifiedDate")
    @Expose
    var modifiedDate : String,

    @SerializedName("postId")
    @Expose
    var postId : Int,

    @SerializedName("userId")
    @Expose
    var userId : Int,

    @SerializedName("nickname")
    @Expose
    var nickname : String,

    @SerializedName("thumbnailURL")
    @Expose
    var thumbnailURL : String

    )
