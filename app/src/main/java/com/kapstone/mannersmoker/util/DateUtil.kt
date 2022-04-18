package com.kapstone.mannersmoker.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val postDateFormat =
        SimpleDateFormat("yyyy/MM/dd a hh:mm", Locale.KOREA)

    fun convertDateToString(date : Date) : String {
        return postDateFormat.format(date)
    }
}