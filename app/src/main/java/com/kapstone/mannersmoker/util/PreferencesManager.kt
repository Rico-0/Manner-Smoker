package com.kapstone.mannersmoker.util

import com.kapstone.mannersmoker.application.GlobalApplication.Companion.prefs

const val FILENAME = "prefs" // application 클래스에 있는 prefs
private const val USER_NAME = "user_name"
private const val USER_PROFILE_IMAGE_URL = "user_profile_image_url"
private const val DAILY_SMOKE = "daily_smoke"
private const val IS_LOGGED_IN_BEFORE = "is_logged_in_before"
private const val IS_GUEST = "is_guest"
private const val ALARM_DAILY_SMOKE = "alarm_daily_smoke"
private const val ALARM_NEAR_TO_SMOKE_PLACE = "alarm_near_to_smoke_place"

object PreferencesManager {
    var user_name : String?
    get() = prefs.getString(USER_NAME, "Unknown User")
    set(value) = prefs.edit().putString(USER_NAME, value).apply()

    var user_profile_image_url : String?
    get() = prefs.getString(USER_PROFILE_IMAGE_URL, "")
    set(value) = prefs.edit().putString(USER_PROFILE_IMAGE_URL, value).apply()

    // 토큰 유효 기간 만료 시 전에 로그인한 기록 있었는지 구분하기 위해 사용
    var is_logged_in_before : Boolean
    get() = prefs.getBoolean(IS_LOGGED_IN_BEFORE, false)
    set(value) = prefs.edit().putBoolean(IS_LOGGED_IN_BEFORE, value).apply()

    // 비회원으로 이용하기를 눌렀는지 여부 체크
    var is_guest : Boolean
    get() = prefs.getBoolean(IS_GUEST, false)
    set(value) = prefs.edit().putBoolean(IS_GUEST, value).apply()

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