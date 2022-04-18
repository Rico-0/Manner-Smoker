package com.kapstone.mannersmoker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

 /*   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그아웃 후 로그인 화면에서 뒤로 가기 누를 시 카카오 로그인 창 뜨는 문제 해결

        // 로그인 정보 확인
        // 비회원으로 이용하기 누른 기록이 있으면
        if (isGuest) {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        } else {
            // 카카오 토큰 확인
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.d("LoginActivity", "카카오 토큰 확인 중 에러 발생 : $error")
                    return@accessTokenInfo
                } else if (tokenInfo == null && loggedIn) {
                    Toast.makeText(
                        this@LoginActivity,
                        "사용자 토큰이 만료되어 재로그인이 필요합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@accessTokenInfo
                } else if (tokenInfo != null) { // 유효한 토큰 존재, 카카오 로그인 성공
                    loggedIn = true
                    Log.d("LoginActivity", "토큰 상태 : 유효")
                    val intent = Intent(this, MyPageActivity::class.java)
                    intent.putExtra("kakaoLogin", true)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else if (tokenInfo == null) {
                    Log.d("LoginActivity", "카카오 토큰 정보 존재하지 않음")
                    return@accessTokenInfo
                }
                // TODO : 네이버 토큰 확인
            }
        }


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
                    else -> { // Unknown
                        //  Toast.makeText(this, "기타 에러 + $error", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (token != null) {
                val intent = Intent(this, MyPageActivity::class.java)
                intent.putExtra("kakaoLogin", true)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }

        val kakao_login_button = findViewById<ImageButton>(R.id.kakao_login_button) // 로그인 버튼
        kakao_login_button.setOnClickListener {
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
        // TODO : 네이버 로그인 기능
        use_app_for_guest.setOnClickListener {
            isGuest = true
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
    } */
}