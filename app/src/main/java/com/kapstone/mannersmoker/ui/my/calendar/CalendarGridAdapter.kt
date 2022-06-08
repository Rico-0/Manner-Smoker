package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.model.data.smoke.DaySmoke
import com.kapstone.mannersmoker.model.data.smoke.DaySmokeWithCalendar
import com.kapstone.mannersmoker.model.data.smoke.Smoke
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class CalendarGridAdapter(private val context: Context, private val calendar: Calendar) :
    BaseAdapter() {

    companion object {
        private const val SIZE_OF_DAY = 7 * 6 // 날짜 전체 개수
        private const val SUNDAY = 0
        private const val SATURDAY = 6
        private val TAG = this::class.java.simpleName
        private val dailySmokeList = mutableListOf<Smoke>()
    }

    private val dailySmokeWithCalendarList = mutableListOf<DaySmokeWithCalendar>()

    init {
        setCalendar()
    }

    fun updateList(list: List<Smoke>) {
        Log.d("adapterr", "list : ${list.size}")
        dailySmokeList.apply {
            clear()
            addAll(list)
        }
        setCalendar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendar() {
        dailySmokeWithCalendarList.clear() // 한 달 단위이므로 호출할 경우 리스트 비우기

        val cal = calendar.clone() as Calendar
        cal.apply {
            set(Calendar.DATE, 1)
            val startOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1
            add(Calendar.DATE, -startOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        while (dailySmokeWithCalendarList.size < SIZE_OF_DAY) {
            val it = dailySmokeList.iterator()
            val oneSmokeData = mutableListOf<Smoke>() // 흡연한 날짜를 모은 하루 흡연량
            Log.d("aaa", "oneSmokeData : ${dailySmokeList.size}")
            while (it?.hasNext() == true) {
                val item = it.next() // 흡연 데이터 한 개 받기
                val itemCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, item.year)
                    set(Calendar.MONTH, item.month - 1)
                    set(Calendar.DATE, item.day)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                dailySmokeWithCalendarList.add(
                    DaySmokeWithCalendar(
                        cal.clone() as Calendar,
                        oneSmokeData
                    )
                )
                cal.add(Calendar.DATE, 1)
            }
            notifyDataSetChanged()
        }
    }

    // 한 갑이 20개비이고 5개 구간으로 나누면 20 / 5 = 4임
    private fun setSmokeIcon(smoke: Int): Drawable? {
        when (smoke) {
            in 1..4 -> {
                return ContextCompat.getDrawable(context, R.drawable.smoke_red5)
            }
            in 5..8 -> {
                return ContextCompat.getDrawable(context, R.drawable.smoke_red4)
            }
            in 9..12 -> {
                return ContextCompat.getDrawable(context, R.drawable.smoke_red3)
            }
            in 13..16 -> {
                return ContextCompat.getDrawable(context, R.drawable.smoke_red2)
            }
            else -> {
                return ContextCompat.getDrawable(context, R.drawable.smoke_red)
            }
        }
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView = view ?: layoutInflater.inflate(R.layout.calendar_day, null)

        val dayTv: TextView = mView.findViewById(R.id.day_tv)
        val dailySmokeIcon: ImageView = mView.findViewById(R.id.daily_smoke_icon)
        val todayCv: CardView = mView.findViewById(R.id.cv_today) // 오늘 날짜에 생기는 빨간 점

        val daySmokeWithCalendar: DaySmokeWithCalendar = getItem(position) as DaySmokeWithCalendar

        val itemDay = daySmokeWithCalendar.calendar.get(Calendar.DATE)

        dayTv.apply {
            text = itemDay.toString()
            when (position % 7) {
                SUNDAY -> setTextColor(ContextCompat.getColor(context, R.color.red)) // 0
                SATURDAY -> setTextColor(ContextCompat.getColor(context, R.color.blue)) // 6
            }
        }

        dailySmokeIcon.setImageDrawable(setSmokeIcon(daySmokeWithCalendar.smokeList.size))
        Log.d("aaa", "흡연량 : ${daySmokeWithCalendar.smokeList.size}")

        daySmokeWithCalendar.calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 오늘 날짜 얻어오기
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 오늘 날짜인 경우 빨간 점 보이게
        if (daySmokeWithCalendar.calendar.timeInMillis == today) {
            Log.v("Today", "TRUE")
            todayCv.visibility = View.VISIBLE
        }

        return mView
    }

    override fun getCount(): Int = dailySmokeWithCalendarList.size

    override fun getItem(position: Int): Any = dailySmokeWithCalendarList[position]

    override fun getItemId(position: Int): Long = position.toLong()

}