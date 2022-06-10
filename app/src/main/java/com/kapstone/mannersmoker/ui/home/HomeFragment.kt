package com.kapstone.mannersmoker.ui.home

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentHomeBinding
import com.kapstone.mannersmoker.util.DateUtil.stringToDate
import com.kapstone.mannersmoker.util.PreferencesManager.daily_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.time_last_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.time_start_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import com.kapstone.mannersmoker.util.PreferencesManager.used_money
import java.util.*
import kotlin.concurrent.thread

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResourceId: Int
    get() = R.layout.fragment_home

    override fun initStartView() {
        setCurrentHealthData()
        setDailySmokeData()
        binding.lastSmokeTime.text = time_last_smoke ?: "데이터 없음"
        binding.firstSmokeTime.text = time_start_smoke ?: "데이터 없음"
        binding.todaySmokeDailySet.text = daily_smoke.toString() + " 개비" // 일일 흡연량 최대
    }

    override fun onResume() {
        super.onResume()
        thread(start = true) {
            requireActivity().runOnUiThread {
                setCurrentHealthData()
                setDailySmokeData()
                binding.firstSmokeTime.text = time_start_smoke ?: "데이터 없음"
            }
        }
    }

    private fun setDailySmokeData() {
        binding.todaySmokeCount.text = today_smoke_amount.toString() // 일일 흡연량 (오늘 핀 담배 개수)
        time_last_smoke?.let {
            binding.lastSmokeTime.text = formatTimeString(stringToDate(time_last_smoke!!)) // 담배 마지막으로 핀 시간으로부터 지난 시간을 알려 줌
        }
        binding.moneySmoke.text = "약 " + used_money.toString() + "원" // 현재까지 담배에 소비한 금액
        binding.todaySmokeDailySet.text = daily_smoke.toString() + " 개비" // 일일 흡연량 최대
    }

    private fun setCurrentHealthData() {
        var backgroundColor : Int
        var face : Drawable?
        var healthText : String
        when (currentHealth(today_smoke_amount)) {
            in 0..20 -> {
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.blue)
                face = ContextCompat.getDrawable(requireContext(), R.drawable.blue_face)
                healthText = "최상"
            }
            in 21..40 -> {
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.green)
                face = ContextCompat.getDrawable(requireContext(), R.drawable.green_face)
                healthText = "좋음"
            }
            in 41..60 -> {
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.yellow)
                face = ContextCompat.getDrawable(requireContext(), R.drawable.yellow_face)
                healthText = "보통"
            }
            in 61..80 -> {
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.orange)
                face = ContextCompat.getDrawable(requireContext(), R.drawable.orange_face)
                healthText = "나쁨"
            }
            else -> {
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
                face = ContextCompat.getDrawable(requireContext(), R.drawable.red_face)
                healthText = "최악"
            }
        }
        binding.fragmentHomeRoot.setBackgroundColor(backgroundColor)
        binding.healthConditionRoot.setBackgroundColor(backgroundColor)
        binding.faceIcon.setImageDrawable(face)
        binding.healthCondition.text = healthText
    }

    private fun currentHealth(smoke : Int) : Int {
        // 100 ~ 80 : 최상, 80 ~ 60 : 상, 60 ~ 40 : 중, 40 ~ 20 : 하, 20 ~ : 최하
       // 백분율 구하는 방법 : ( 100 * value ) / max
        return (100 * smoke) / daily_smoke
    }

    private fun formatTimeString(tempDate : Date) : String {

        if (tempDate.equals("")) // 데이터 없음
            return "데이터 없음"

        val curTime : Long = System.currentTimeMillis()
        val regTime : Long = tempDate.time
        var diffTime : Long = (curTime - regTime) / 1000

        var msg = ""
        if (diffTime < SEC)
            msg = "방금 전"
        else {
            diffTime /= SEC
            if (diffTime < MIN) {
                msg = diffTime.toString() + "분 전"
                return msg
            }
            diffTime /= MIN
            if (diffTime < HOUR) {
                msg = diffTime.toString() + "시간 전"
                return msg
            }
            diffTime /= HOUR
            if (diffTime < DAY) {
                msg = diffTime.toString() + "일 전"
                return msg
            }
            diffTime /= DAY
            if (diffTime < MONTH) {
                msg = diffTime.toString() + "달 전"
                return msg
            } else {
                msg = diffTime.toString() + "년 전"
                return msg
            }
        }
        return msg
    }

    companion object {
        private const val SEC = 60
        private const val MIN = 60
        private const val HOUR = 24
        private const val DAY = 30
        private const val MONTH = 12
    }

}