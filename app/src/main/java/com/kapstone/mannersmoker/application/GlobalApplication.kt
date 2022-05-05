package com.kapstone.mannersmoker.application

import android.app.Application
import android.content.SharedPreferences
import com.kakao.sdk.common.KakaoSdk
import com.kapstone.mannersmoker.model.data.DailySmokeData
import com.kapstone.mannersmoker.model.data.Place
import com.kapstone.mannersmoker.model.data.Places.places
import com.kapstone.mannersmoker.ui.my.SettingFragment.Companion.isPassedOneDay
import com.kapstone.mannersmoker.util.FILENAME

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
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    private fun initLocations() {
        places.add(
            Place(37.25108002,127.0198291, "")
        )
    }

}