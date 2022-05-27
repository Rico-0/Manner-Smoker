package com.kapstone.mannersmoker.util

import com.kakao.sdk.auth.model.OAuthToken
import com.kapstone.mannersmoker.application.GlobalApplication.Companion.prefs

const val FILENAME = "prefs" // application 클래스에 있는 prefs
private const val USER_ID = "user_id"
private const val USER_PROFILE_IMAGE = "user_profile_image"
private const val DAILY_SMOKE = "daily_smoke"
private const val IS_LOGGED_IN_BEFORE = "is_logged_in_before"
private const val LOGIN_TYPE = "login_type"
private const val PERMISSION_CHECKED = "permission_checked"
private const val ALARM_DAILY_SMOKE = "alarm_daily_smoke"
private const val ALARM_NEAR_TO_SMOKE_PLACE = "alarm_near_to_smoke_place"

object PreferencesManager {

    var user_id : String?
    get() = prefs.getString(USER_ID, "")
    set(value) = prefs.edit().putString(USER_ID, value).apply()

    var user_profile_image : String?
    get() = prefs.getString(USER_PROFILE_IMAGE, "")
        set(value) = prefs.edit().putString(USER_PROFILE_IMAGE, value).apply()

    // 토큰 유효 기간 만료 시 전에 로그인한 기록 있었는지 구분하기 위해 사용
    var is_logged_in_before : Boolean
    get() = prefs.getBoolean(IS_LOGGED_IN_BEFORE, false)
    set(value) = prefs.edit().putBoolean(IS_LOGGED_IN_BEFORE, value).apply()

    // 로그인 유형 (kakao)
    var login_type : String?
    get() = prefs.getString(LOGIN_TYPE, "null")
    set(value) = prefs.edit().putString(LOGIN_TYPE, value).apply()

    // 권한 부여가 되었는지 확인
    var isForegroundPermissionChecked : Boolean
    get() = prefs.getBoolean(PERMISSION_CHECKED, false)
        set(value) = prefs.edit().putBoolean(PERMISSION_CHECKED, value).apply()

    // 사용자가 직접 설정하는 하루 흡연량
    var daily_smoke : Int
    get() = prefs.getInt(DAILY_SMOKE, 10)
    set(value) = prefs.edit().putInt(DAILY_SMOKE, value).apply()

    // 일일 흡연량 알림
    var alarm_daily_smoke : Boolean
        get() = prefs.getBoolean(ALARM_DAILY_SMOKE, true)
        set(value) = prefs.edit().putBoolean(ALARM_DAILY_SMOKE, value).apply()

    // 흡연 구역 근방에 위치할 시 알림
    var alarm_near_to_smoke_place : Boolean
        get() = prefs.getBoolean(ALARM_NEAR_TO_SMOKE_PLACE, true)
        set(value) = prefs.edit().putBoolean(ALARM_NEAR_TO_SMOKE_PLACE, value).apply()

}