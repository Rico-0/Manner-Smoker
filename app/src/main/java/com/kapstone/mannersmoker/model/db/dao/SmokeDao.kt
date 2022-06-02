package com.kapstone.mannersmoker.model.db.dao

import com.kapstone.mannersmoker.model.data.SmokeAreaDataClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmokeDao {
  /*  @GET("api/smoke-amount/{userId}")
    fun getDaySmokeData(day : Int, month : Int, year : Int, @Path("userId") userId : String) : Call<>

    @POST("api/smoke-amount")
    fun sendSmokeData(userId : String) : Completable

    @GET("api/smoke-amount/month/{userId}")
    fun getMonthSmokeData(month : Int, year : Int, @Path("userId") userId : String) : Single<List<List<OneSmoke>>> */

    @GET("area/{area}")
    fun getSmokeArea(@Path("area") areaCode : Int) : Call<SmokeAreaDataClass>

    @GET("area/all")
    fun getAllSmokeArea() : Call<SmokeAreaDataClass>

}