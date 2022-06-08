package com.kapstone.mannersmoker.model.data

import android.util.Log
import com.kapstone.mannersmoker.model.data.user.Token
import com.kapstone.mannersmoker.util.PreferencesManager.access_token
import com.kapstone.mannersmoker.util.PreferencesManager.refresh_token
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenAddedRequest = chain.request().putTokenHeader(access_token ?: "")
        val response = chain.proceed(tokenAddedRequest)

        // 서버 응답 코드가 토큰이 만료되었는지 확인
        if (response.code == 401) {
           access_token = refresh_token
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