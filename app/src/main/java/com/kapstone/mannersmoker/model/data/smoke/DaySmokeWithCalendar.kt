package com.kapstone.mannersmoker.model.data.smoke

import java.util.*

data class DaySmokeWithCalendar(
    val calendar : Calendar,
    var smokeList : List<Smoke>
)
