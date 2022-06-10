package com.kapstone.mannersmoker.ui.main

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityLoginSelectBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.user.Token
import com.kapstone.mannersmoker.model.data.user.User
import com.kapstone.mannersmoker.util.PreferencesManager.access_token
import com.kapstone.mannersmoker.util.PreferencesManager.is_logged_in_before
import com.kapstone.mannersmoker.util.PreferencesManager.kakao_access_token
import com.kapstone.mannersmoker.util.PreferencesManager.refresh_token
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginSelectActivity : BaseActivity2<ActivityLoginSelectBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_login_select

    private val smokeDao = RetrofitInstance.smokeDao

    // 카카오 로그인 콜백
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Toast.makeText(this, "접근이 거부됨(동의 취소)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                        .show()
                }
                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                        .show()
                }
                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                }
                error.toString() == ClientErrorCause.Cancelled.toString() -> {
                    Toast.makeText(this, "카카오 로그인을 취소하였습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this, "기타 에러 + $error", Toast.LENGTH_LONG).show()
                }
            }
        } else if (token != null) {
            Log.d(TAG, "카카오 토큰 값 : ${token.accessToken}")
            smokeDao.login(token.accessToken).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    val token = response.body()
                    Log.d(TAG, "토큰 불러오기 코드값 : ${response.code()}")
                   // access_token = token?.token.accessToken
                   // refresh_token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJja3k2ODA4MUBuYXZlci5jb20iLCJpYXQiOjE2NTQ2OTMwMjcsImV4cCI6MTY1NTI5NzgyN30.ITGmfn7H2TIoMjelyde9Z5nqk-awYhr3RNjatdi5e_U"

                    token?.let {
                        if (token.token.accessToken != null) {
                            Log.d(TAG, "서버에서 받아온 토큰 값 : ${token.token.accessToken}")
                            access_token = token.token.accessToken
                            refresh_token = token.token.refreshToken
                                smokeDao.getUserInfo(access_token!!).enqueue(object : Callback<User> {
                                    override fun onResponse(
                                        call: Call<User>,
                                        response: Response<User>
                                    ) {
                                        val userInfo = response.body()
                                        userInfo?.user?.userId?.let {
                                            user_id_from_server = userInfo.user.userId
                                            Log.d(TAG, "유저 아이디 : $user_id_from_server")
                                        }
                                    }

                                    override fun onFailure(call: Call<User>, t: Throwable) {
                                        Log.d(TAG, "유저 아이디 받아오기 실패 : $t")
                                    }
                                })
                        } else {
                            Log.d(TAG, "토큰 값 없음")
                        }
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Log.d(TAG, "토큰 값 불러오기 실패 : $t")
                }
            })

            Log.d(TAG, "인증에 성공하였습니다. 토큰 값 : $access_token")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("loginType", "kakao")
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    override fun initStartView() {
        binding.kakaoLoginButton.setOnClickListener {

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.d("LoginSelectActivity", "카카오 토큰 확인 중 에러 발생 : $error")
                    return@accessTokenInfo
                } else if (tokenInfo != null) { // 유효한 토큰 존재, 카카오 로그인 성공

                } else if (tokenInfo == null) {
                    Log.d("LoginSelectActivity", "카카오 토큰 정보 존재하지 않음")
                    return@accessTokenInfo
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
    }
}