package com.kapstone.mannersmoker.application

import android.app.Application
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.common.KakaoSdk
import com.kapstone.mannersmoker.databinding.ProgressbarLayoutBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.SmokeAreaDataClass
import com.kapstone.mannersmoker.model.data.SmokeAreaModel
import com.kapstone.mannersmoker.model.data.SmokeAreaModels
import com.kapstone.mannersmoker.model.data.SmokeAreaModels.allSmokeAreaList
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.ui.my.SettingActivity.Companion.isPassedOneDay
import com.kapstone.mannersmoker.util.FILENAME
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.is_setted_daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GlobalApplication : Application() {

    private lateinit var smokeDao: SmokeDao

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