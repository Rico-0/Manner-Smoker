package com.kapstone.mannersmoker.ui.my.calendar

import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityCalendarSmokeHistoryBinding

class SmokeCalendarActivity : BaseActivity2<ActivityCalendarSmokeHistoryBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_calendar_smoke_history

    override fun initStartView() {
        binding.smokeCalendar.addDecorators(Decorator.SundayDecorator(), Decorator.SaturdayDecorator())
    }
}