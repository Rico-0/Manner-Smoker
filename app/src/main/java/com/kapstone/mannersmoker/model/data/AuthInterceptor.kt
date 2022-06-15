package com.kapstone.mannersmoker.model.data

import android.util.Log
import com.kapstone.mannersmoker.model.data.user.Token
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.util.PreferencesManager.access_token
import com.kapstone.mannersmoker.util.PreferencesManager.refresh_token
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenAddedRequest = chain.request().putTokenHeader(access_token ?: "")
        val response = chain.proceed(tokenAddedRequest)

        // 서버 응답 코드가 토큰이 만료되었는지 확인
        if (response.code == 401) {
          val retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-37-250-127.ap-northeast-2.compute.amazonaws.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SmokeDao::class.java)
                refresh_token?.let { retrofit.refreshToken(it).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: retrofit2.Response<Token>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            access_token = response.body()?.token?.accessToken
                            refresh_token = response.body()?.token?.refreshToken
                        }
                    }
                }
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Log.d("Interceptor" , "토큰 갱신 실패")
                 }
            }) }
            val refreshedRequest = chain.request().putTokenHeader(access_token ?: "")
            return chain.proceed(refreshedRequest)
        } else return response
    }

    private fun Request.putTokenHeader(accessToken : String): Request {
        return this.newBuilder()
            .addHeader(AUTHORIZATION, accessToken)
            .build()
    }

    companion object {
        private const val AUTHORIZATION = "authorization"
    }
}