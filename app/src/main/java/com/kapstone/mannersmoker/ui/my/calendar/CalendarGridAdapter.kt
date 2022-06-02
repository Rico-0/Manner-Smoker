package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.model.data.DaySmoke
import com.kapstone.mannersmoker.model.data.Smoke
import com.kapstone.mannersmoker.util.DateUtil
import java.util.*

class CalendarGridAdapter(private val context : Context, private val calendar : Calendar) : BaseAdapter() {

    companion object {
        private const val SIZE_OF_DAY = 7 * 6 // 날짜 전체 개수
        private const val SUNDAY = 0
        private const val SATURDAY = 6
        private val oneSmokeList = mutableListOf<Smoke>() // 일별 흡연량 데이터
        private val TAG = this::class.java.simpleName
    }

    private val dailySmokeList = mutableListOf<DaySmoke>()

     init {
        setCalendar()
    }

   fun updateList(list : List<Smoke>) {
        oneSmokeList.apply {
            clear()
            addAll(list)
        }
        setCalendar()
    }

    private fun setCalendar() {
        dailySmokeList.clear() // 한 달 단위이므로 호출할 경우 리스트 비우기

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

        while (dailySmokeList.size < SIZE_OF_DAY) {
           val it = oneSmokeList.iterator()
            val oneSmokeData = mutableListOf<Smoke>()
            while (it.hasNext()) {
                val item = it.next()
                if (DateUtil.isCalendarAndDateSame(cal, item.createDate)) {
                    oneSmokeData.add(item)
                }
            }
            dailySmokeList.add(DaySmoke(cal.clone() as Calendar))
            cal.add(Calendar.DATE, 1)
        }
        notifyDataSetChanged()
    }

    // 한 갑이 20개비이고 5개 구간으로 나누면 20 / 5 = 4임
    private fun setSmokeIcon(smoke : Int) : Drawable? {
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
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView = view ?: layoutInflater.inflate(R.layout.calendar_day, null)

        val dayTv : TextView = mView.findViewById(R.id.day_tv)
        val dailySmokeIcon : ImageView = mView.findViewById(R.id.daily_smoke_icon)
        val todayCv: CardView = mView.findViewById(R.id.cv_today) // 오늘 날짜에 생기는 빨간 점

        val daySmoke : DaySmoke = getItem(position) as DaySmoke

        val itemDay = daySmoke.calendar.get(Calendar.DATE)

        dayTv.apply {
            text = itemDay.toString()
            when (position % 7) {
                SUNDAY -> setTextColor(ContextCompat.getColor(context, R.color.red)) // 0
                SATURDAY -> setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
        }

        daySmoke.calendar.apply {
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
        if (daySmoke.calendar.timeInMillis == today){
            Log.v("Today", "TRUE")
            todayCv.visibility = View.VISIBLE
        }

        // 핀 담배 개수에 따라 아이콘 다르게 세팅 (담배 안 핀 날은 설정 X)
       // dailySmokeIcon.setImageDrawable(setSmokeIcon(daySmoke.smokeList.size))

        return mView
    }

    override fun getCount(): Int = dailySmokeList.size

    override fun getItem(position: Int): Any = dailySmokeList[position]

    override fun getItemId(position: Int): Long = position.toLong()

}