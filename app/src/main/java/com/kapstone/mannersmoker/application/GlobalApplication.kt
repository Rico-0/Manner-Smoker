package com.kapstone.mannersmoker.application

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.smoke.Smoke
import com.kapstone.mannersmoker.model.data.user.Token
import com.kapstone.mannersmoker.model.data.user.User
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.ui.my.SettingActivity.Companion.isPassedOneDay
import com.kapstone.mannersmoker.util.FILENAME
import com.kapstone.mannersmoker.util.PreferencesManager.access_token
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.is_setted_daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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
        if (isPassedOneDay()) {
            is_setted_daily_smoke = !is_setted_daily_smoke
            today_smoke_amount = 0
            daily_smoke = 10
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

}