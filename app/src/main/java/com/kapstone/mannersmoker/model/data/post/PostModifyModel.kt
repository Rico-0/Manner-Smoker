package com.kapstone.mannersmoker.model.data.post

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostModifyModel(
    @SerializedName("content")
    @Expose
    var content : String
)
