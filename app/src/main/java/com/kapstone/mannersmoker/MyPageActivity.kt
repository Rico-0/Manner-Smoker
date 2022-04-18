package com.kapstone.mannersmoker

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
//import com.kapstone.mannersmoker.databinding.ActivityMypageBinding
import com.kapstone.mannersmoker.util.ClickUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_name
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image_url
import com.kapstone.mannersmoker.util.PreferencesManager.is_guest

class MyPageActivity : AppCompatActivity() {
/*
    private lateinit var binding: ActivityMypageBinding
    private var kakaoLogin: Boolean = false

    // private var naverLogin : Boolean = intent.getBooleanExtra("naverLogin", false)
    var isLogin: Boolean = false

    val click by lazy { ClickUtil(this.lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage)
        kakaoLogin = intent.getBooleanExtra("kakaoLogin", false)

        Log.d("MyPageActivity", user_name + ", " + user_profile_image_url)
        Log.d("MyPageActivity", "" + isLogin)

        // 카카오톡으로 로그인 시도한 경우
        if (user_name.equals("Unknown User") && user_profile_image_url.equals("") && kakaoLogin) {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("MypageActivity", "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    user_name = user.kakaoAccount?.profile?.nickname
                    binding.profileName.text = user_name
                    user_profile_image_url = user.kakaoAccount?.profile?.thumbnailImageUrl
                    binding.loginTypeIcon.setImageResource(R.drawable.kakaotalk_image)
                    binding.loginTypeText.text = resources.getString(R.string.kakao_login)
                    Glide.with(this@MyPageActivity)
                        .load(user_profile_image_url)
                        .error(R.drawable.my) // 에러 발생 시 기본 이미지
                        .into(binding.proflieImage)
                    isLogin = true
                    binding.isLogin = isLogin

                } else if (user == null) { // 로그인 기록이 없는 경우
                    isLogin = false
                    binding.isLogin = isLogin
                    Glide.with(this@MyPageActivity)
                        .load(R.drawable.my)
                        .into(binding.proflieImage)
                    Toast.makeText(this, "유저 정보를 받아오는 데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (user_name.equals("Unknown User") && user_profile_image_url.equals("")) { // 비회원으로 이용하기를 누른 경우
            isLogin = false
            binding.isLogin = isLogin
            binding.profileName.text = user_name
            Glide.with(this@MyPageActivity)
                .load(R.drawable.my)
                .error(R.drawable.my)
                .into(binding.proflieImage)
        } else { // 예상치 못한 흐름 처리
           // is_guest = !is_guest
         //   val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
          //  startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
          //  finish()
        }

        // 로그아웃 (카카오, 네이버 분기 처리)
        binding.logout.setOnClickListener {
            if (kakaoLogin) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        user_name = "Unknown User"
                        user_profile_image_url = ""
                        kakaoLogin = false
                        Toast.makeText(this, "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
                        // 스택 중간에 있었던 액티비티들을 지우는 역할
                        startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
            } // else (login_type.equals("naver")) { ...
        }

        // 회원 탈퇴 (카카오, 네이버 분기 처리)
        binding.deleteAccount.setOnClickListener {
            val alterDialog = AlertDialog.Builder(this)
                .setMessage("회원 탈퇴 시 모든 데이터 복구가 불가능합니다.\n 정말 회원 탈퇴를 진행하시겠습니까?")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (kakaoLogin) {
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        this@MyPageActivity,
                                        "회원 탈퇴 실패 $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    user_name = "Unknown User"
                                    user_profile_image_url = ""
                                    kakaoLogin = false
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        this@MyPageActivity,
                                        "정상적으로 회원 탈퇴가 완료되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@MyPageActivity, LoginActivity::class.java)
                                    // 스택 중간에 있었던 액티비티들을 지우는 역할
                                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                                    finish()
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("아니오", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.let {
                            it.dismiss()
                        }
                    }
                })
            alterDialog.show()
        }

        binding.loginButtonLater.setOnClickListener {
            val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
            // 스택 중간에 있었던 액티비티들을 지우는 역할
            is_guest = !is_guest
            startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    fun goToLoginActivity() {
        val intent = Intent(this@MyPageActivity, MainActivity::class.java)
        // 스택 중간에 있었던 액티비티들을 지우는 역할
        is_guest = !is_guest
        startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    } */
}


