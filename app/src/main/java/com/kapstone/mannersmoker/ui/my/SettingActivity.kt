package com.kapstone.mannersmoker.ui.my

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivitySettingBinding
import com.kapstone.mannersmoker.util.AlarmReceiver
import com.kapstone.mannersmoker.util.DateUtil.dateToString
import com.kapstone.mannersmoker.util.DateUtil.stringToDate
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.alarm_daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.alarm_near_to_smoke_place
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke_time
import com.kapstone.mannersmoker.util.PreferencesManager.is_setted_daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.time_start_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import java.text.SimpleDateFormat
import java.util.*

class SettingActivity : BaseActivity2<ActivitySettingBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_setting


    override fun initStartView() {
        // 어플을 킨 시점과 흡연량을 설정한 시간 차이가 하루 날 경우 기본값 10개비로 세팅
        if (isPassedOneDay()) {
            binding.dailySmoke.text = "10개비"
            daily_smoke = 10
            is_setted_daily_smoke = false
        }
        initDailySmoke()
        initFirstSmokeTime()
        initAlarmSwitch()
        setAlarmSwitchChangeListener()
    }

    private fun initDailySmoke() {
        binding.dailySmoke.text = daily_smoke.toString() + "개비" // 기본 10개비
        binding.setDailySmokeButton.setOnClickListener {
            val isPassedOneDay = isPassedOneDay()
            // 흡연량을 이미 설정한 경우
            if (is_setted_daily_smoke && !isPassedOneDay) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("흡연량 설정 알림")
                    .setMessage("이미 오늘의 흡연량을 설정하셨습니다.")
                    .setPositiveButton("확인", null)
                builder.show()
            } else {
                showDialog()
            }
        }
    }

    // TODO : 사용자가 지정한 날짜 이전에 흡연 데이터가 존재할 경우 그 이전의 금액은 차감시켜야 함
    // TODO : 이거는 백엔드에서 데이터 받아오면 구현 ㄱㄱ
    private fun initFirstSmokeTime() {
        var dateString = ""
        binding.setFirstSmokeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                time_start_smoke = dateString
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun showDialog() {
        val dialog = SettingDailySmokeDialog(this)
        dialog.setAcceptBtnClickListener { dailySmoke ->
            daily_smoke = dailySmoke
            binding.dailySmoke.text = daily_smoke.toString() + "개비"
            is_setted_daily_smoke = true
            today_smoke_amount = 0
            val date = Date()
            Log.d(TAG, "흡연량 설정한 시간 : ${dateToString(date)}")
            daily_smoke_time = dateToString(date) // 흡연량 설정한 시간 저장
        }
        dialog.setDialog()
    }

    private fun initAlarmSwitch() {
        binding.alarmDailySmokeSwitch.apply {
            isChecked = alarm_daily_smoke
        }
    }

    private fun setAlarmSwitchChangeListener() {
        binding.alarmDailySmokeSwitch.setOnClickListener {
            onClickDailySmokeSwitch()
        }
    }

    fun onClickDailySmokeSwitch() {
        alarm_daily_smoke != alarm_daily_smoke
        if (alarm_daily_smoke) {
            setDailySmokeAlarm(9) // 9시에 알람 설정
        } else {
            //알람해제
            val intent =
                Intent(this, AlarmReceiver::class.java)  // 1. 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정
            PendingIntent.getBroadcast(     // 2 PendingIntent가 이미 존재할 경우 cancel 하고 다시 생성
                this,
                AlarmReceiver.DAILY_SMOKE_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT //PendingIntent 객체가 이미 존재할 경우, 기존의 ExtraData 를 모두 삭제
            ).run {
                cancel()
            }
        }
    }

    private fun setDailySmokeAlarm(hour: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(this, AlarmReceiver::class.java)  // 1. 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정합니다.
        val pendingIntent =
            PendingIntent.getBroadcast(     // 2 PendingIntent가 이미 존재할 경우 cancel 하고 다시 생성
                this,
                AlarmReceiver.DAILY_SMOKE_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT //PendingIntent 객체가 이미 존재할 경우, 기존의 ExtraData 를 모두 삭제
            )

        val calendar: Calendar =
            Calendar.getInstance().apply { // 3. Calendar 객체를 생성하여 알람이 울릴 정확한 시간을 설정합니다.
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
            }
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            else -> alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        fun isPassedOneDay(): Boolean { // 흡연량 설정한 시간으로부터 1일이 경과했는지 계산
            if (!daily_smoke_time.equals("")) {
                var today = Date()
                var lastSetDate = stringToDate(daily_smoke_time!!)
                var calcuDate = (today.time - lastSetDate.time) / (60 * 60 * 24 * 1000)
                Log.d("SettingActivity", "calcuDate : $calcuDate")
                return (calcuDate.toInt() == 1)
            } else return false
        }
    }
}