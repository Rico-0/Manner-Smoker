package com.kapstone.mannersmoker.util

import java.text.SimpleDateFormat
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

    fun isCalendarAndDateSame(calendar: Calendar, date : Date) : Boolean {
        val calendarOfDate = Calendar.getInstance()
        calendarOfDate.time = date
        return calendar.get(Calendar.YEAR)==calendarOfDate.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH)==calendarOfDate.get(Calendar.MONTH)
                && calendar.get(Calendar.DATE)==calendarOfDate.get(Calendar.DATE)
    }
}