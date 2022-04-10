package com.kapstone.mannersmoker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_mypage.*

class MyPageActivity : AppCompatActivity() {

    private var imageUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mypage)

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("MypageActivity", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                kakao_profile_name.text = user.kakaoAccount?.profile?.nickname
                imageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                Glide.with(this@MyPageActivity)
                    .load(imageUrl)
                    .error(R.drawable.my)
                    .into(kakao_proflie_image)
                Log.d("imageUrl", "imageUrl = $imageUrl")
            }
        }
    }
}