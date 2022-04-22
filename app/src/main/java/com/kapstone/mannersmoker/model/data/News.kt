package com.kapstone.mannersmoker.model.data

data class News(
    var title : String,
    var content : String, // 내용 미리 보기
    var thumbnailUrl : String,
    var originalContentUrl : String // 기사 url 주소
)
