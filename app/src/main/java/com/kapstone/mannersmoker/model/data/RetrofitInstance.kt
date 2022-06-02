package com.kapstone.mannersmoker.model.data

import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 둘다 by lazy 로 늦은 초기화 해줌으로써
// smokeDao 변수가 사용될 때 초기화되고, 그 안에서 retrofit 변수를 사용하기 때문에 초기화 된다.

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://ec2-3-37-250-127.ap-northeast-2.compute.amazonaws.com:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val smokeDao : SmokeDao by lazy {
        retrofit.create(SmokeDao::class.java)
    }
}