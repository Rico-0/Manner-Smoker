package com.kapstone.mannersmoker.application

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kapstone.mannersmoker.model.data.DailySmokeData
import com.kapstone.mannersmoker.model.data.Place
import com.kapstone.mannersmoker.model.data.Places.places
import com.kapstone.mannersmoker.ui.main.MainActivity
import com.kapstone.mannersmoker.ui.my.SettingActivity.Companion.isPassedOneDay
import com.kapstone.mannersmoker.util.FILENAME
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.is_logged_in_before

class GlobalApplication : Application() {

    companion object {
        var instance : GlobalApplication? = null
        lateinit var prefs : SharedPreferences

        fun getGlobalApplicationContext() : GlobalApplication? {
            checkNotNull(this) {
                "this application does not inherit com.kakao.GlobalApplication"
            }
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSdk.init(this, "d1134cd947745b49f0d8c93f0dd4fe81")
        prefs = getSharedPreferences(FILENAME, 0) // 사용자 설정값을 얻어옴
        initLocations()
        if (isPassedOneDay())
            DailySmokeData.isSettedDailySmoke = !DailySmokeData.isSettedDailySmoke

        // 카카오 토큰 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.d("ApplicationClass", "카카오 토큰 확인 중 에러 발생 : $error")
                return@accessTokenInfo
            } else if (tokenInfo == null && is_logged_in_before) {
                Toast.makeText(
                    this,
                    "사용자 토큰이 만료되어 재로그인이 필요합니다.",
                    Toast.LENGTH_LONG
                ).show()
                return@accessTokenInfo
            } else if (tokenInfo != null) { // 유효한 토큰 존재
                is_logged_in_before = true
                Log.d("ApplicationClass", "토큰 상태 : 유효")
            } else if (tokenInfo == null) {
                Log.d("ApplicationClass", "카카오 토큰 정보 존재하지 않음")
                return@accessTokenInfo
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    // Todo : places 데이터에 백엔드에서 불러올 것
    private fun initLocations() {
        places.add(
            Place(37.25108002,127.0198291, "")
        )
    }

}