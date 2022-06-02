package com.kapstone.mannersmoker.util

import com.kapstone.mannersmoker.application.GlobalApplication.Companion.prefs

const val FILENAME = "prefs" // application 클래스에 있는 prefs
private const val USER_ID = "user_id"
private const val USER_PROFILE_IMAGE = "user_profile_image"
private const val DAILY_SMOKE = "daily_smoke"
private const val IS_LOGGED_IN_BEFORE = "is_logged_in_before"
private const val LOGIN_TYPE = "login_type"
private const val PERMISSION_CHECKED = "permission_checked"
private const val FIRST_SMOKE_TIME = "first_smoke_time"
private const val ALARM_DAILY_SMOKE = "alarm_daily_smoke"
private const val ALARM_NEAR_TO_SMOKE_PLACE = "alarm_near_to_smoke_place"
private const val SET_DAILY_SMOKE = "set_daily_smoke"
private const val DAILY_SMOKE_TIME = "daily_smoke_time"
private const val TIME_LAST_SMOKE = "time_last_smoke"
private const val TIME_START_SMOKE = "time_start_smoke"
private const val USED_MONEY = "used_money"
private const val TODAY_SMOKE_AMOUNT = "today_smoke_amount"
private const val NOTIFICATION_SMOKE = "notification_smoke"

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

    // 흡연 처음 시작한 날짜 설정했는지 확인
    var is_setted_first_smoke_time : Boolean
    get() = prefs.getBoolean(FIRST_SMOKE_TIME, false)
    set(value) = prefs.edit().putBoolean(FIRST_SMOKE_TIME, value).apply()

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

    // 일일 흡연량 설정했는지 체크
    var is_setted_daily_smoke : Boolean
    get() = prefs.getBoolean(SET_DAILY_SMOKE, false)
    set(value) = prefs.edit().putBoolean(SET_DAILY_SMOKE, value).apply()

    // 일일 흡연량 설정한 시간
    var daily_smoke_time : String?
    get() = prefs.getString(DAILY_SMOKE_TIME, "")
    set(value) = prefs.edit().putString(DAILY_SMOKE_TIME, value).apply()

    // 마지막으로 흡연한 시간
    var time_last_smoke: String?
        get() = prefs.getString(TIME_LAST_SMOKE, null)
        set(value) = prefs.edit().putString(TIME_LAST_SMOKE, value).apply()

    // 최초 흡연 시작 날짜
    var time_start_smoke: String?
        get() = prefs.getString(TIME_START_SMOKE, null)
        set(value) = prefs.edit().putString(TIME_START_SMOKE, value).apply()

    // 지금까지 흡연에 사용한 금액
    var used_money : Int
        get() = prefs.getInt(USED_MONEY, 0)
        set(value) = prefs.edit().putInt(USED_MONEY, value).apply()

    // 하루에 담배 몇 개 피웠는지 개수 (일일 흡연량 대비 이 개수로 건강 상태가 결정됨)
    var today_smoke_amount: Int
        get() = prefs.getInt(TODAY_SMOKE_AMOUNT, 0)
        set(value) = prefs.edit().putInt(TODAY_SMOKE_AMOUNT, value).apply()

    // 흡연 중인지 체크하는 알림 제어 시 사용
    var notification_smoke : Boolean
        get() = prefs.getBoolean(NOTIFICATION_SMOKE, true)
        set(value) = prefs.edit().putBoolean(NOTIFICATION_SMOKE, value).apply()
}