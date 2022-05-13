package com.kapstone.mannersmoker.ui.my

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentSettingBinding
import com.kapstone.mannersmoker.model.data.DailySmokeData
import com.kapstone.mannersmoker.ui.main.findNavControllerSafely
import com.kapstone.mannersmoker.util.AlarmReceiver
import com.kapstone.mannersmoker.util.PreferencesManager.alarm_daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.alarm_near_to_smoke_place
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke
import java.text.SimpleDateFormat
import java.util.*

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private lateinit var callback : OnBackPressedCallback

    override val layoutResourceId: Int
        get() = R.layout.fragment_setting

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavControllerSafely()?.navigate(R.id.action_go_to_main)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initStartView() {
        // 어플을 킨 시점과 흡연량을 설정한 시간 차이가 하루 날 경우 기본값 10개비로 세팅
        if (isPassedOneDay()){
            binding.dailySmoke.text = "10개비"
            daily_smoke = 10
        }
        initDailySmoke()
        initAlarmSwitch()
        setAlarmSwitchChangeListener()
        }

    private fun initDailySmoke() {
        binding.dailySmoke.text = daily_smoke.toString() + "개비" // 기본 10개비
        binding.setDailySmokeButton.setOnClickListener {
            val isPassedOneDay = isPassedOneDay()
            // 흡연량을 이미 설정한 경우
            if (DailySmokeData.isSettedDailySmoke && !isPassedOneDay) {
               val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("흡연량 설정 알림")
                       .setMessage("이미 오늘의 흡연량을 설정하셨습니다.")
                    .setPositiveButton("확인", null)
                builder.show()
            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        val dialog = SettingDailySmokeDialog(requireContext())
        dialog.setAcceptBtnClickListener { dailySmoke ->
            daily_smoke = dailySmoke
            binding.dailySmoke.text = daily_smoke.toString() + "개비"
            DailySmokeData.isSettedDailySmoke = true
        }
        dialog.setDialog()
    }

    private fun initAlarmSwitch() {
        binding.alarmDailySmokeSwitch.apply {
            isChecked = alarm_daily_smoke
        }
        binding.alarmSmokePlaceSwitch.apply {
            isChecked = alarm_near_to_smoke_place
        }
    }

    private fun setAlarmSwitchChangeListener() {
        binding.alarmDailySmokeSwitch.setOnClickListener {
            onClickDailySmokeSwitch()
        }
        binding.alarmSmokePlaceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarm_near_to_smoke_place = true
            } else {
                alarm_near_to_smoke_place = !alarm_near_to_smoke_place
            }
        }
    }

    fun onClickDailySmokeSwitch() {
        alarm_daily_smoke != alarm_daily_smoke
        if (alarm_daily_smoke) {
            setDailySmokeAlarm(9) // 9시에 알람 설정
        }
        else {
            //알람해제
            val intent = Intent(activity, AlarmReceiver::class.java)  // 1. 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정
            PendingIntent.getBroadcast(     // 2 PendingIntent가 이미 존재할 경우 cancel 하고 다시 생성
                activity,
                AlarmReceiver.DAILY_SMOKE_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT //PendingIntent 객체가 이미 존재할 경우, 기존의 ExtraData 를 모두 삭제
            ).run {
                cancel()
            }
        }
    }

    private fun setDailySmokeAlarm(hour: Int) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(activity, AlarmReceiver::class.java)  // 1. 알람 조건이 충족되었을 때, 리시버로 전달될 인텐트를 설정합니다.
        val pendingIntent = PendingIntent.getBroadcast(     // 2 PendingIntent가 이미 존재할 경우 cancel 하고 다시 생성
            activity,
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

    companion object {
        fun isPassedOneDay() : Boolean {
            if (DailySmokeData.setDailySmokeTime != null) {
                var today = Calendar.getInstance().time
                val sf = SimpleDateFormat("yyyy-MM-dd 00:00:00")
                var lastSetDate = sf.parse(DailySmokeData.setDailySmokeTime?.time.toString())
                var calcuDate = (today.time - lastSetDate.time).toInt()
                return calcuDate == 1
            } else return false
        }
    }
}

