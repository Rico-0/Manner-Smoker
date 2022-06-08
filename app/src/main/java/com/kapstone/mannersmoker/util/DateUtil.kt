package com.kapstone.mannersmoker.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtil {

    fun stringToDate(date : String) : Date {
        val fm = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
        return fm.parse(date)
    }

    fun dateToString(str : Date) : String {
        val fm = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
        return fm.format(str)
    }

    fun convertCalendarToString(calendar: Calendar, format : String) : String{
        val simpleFormat = SimpleDateFormat(format, Locale.KOREA)
        return simpleFormat.format(calendar.time)
    }

    fun isMonthSame(c1 : Calendar, c2 : Calendar) : Boolean {
        return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
    }

    fun isCalendarSame(calendar1: Calendar, calendar2: Calendar) : Boolean {
        return calendar1.get(Calendar.YEAR)==calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH)==calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DATE)==calendar2.get(Calendar.DATE)
    }

    @RequiresApi(Build.VERSION_CODES.O) // 오레오 이상부터 가능 (추후 수정)
    fun LocalDateTimeToString(str : String) : String {
        val localDateTime : LocalDateTime = LocalDateTime.parse(str)
        val fm : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a HH시 mm분", Locale.KOREA)
        return fm.format(localDateTime)
    }

}