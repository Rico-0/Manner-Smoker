package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.model.data.smoke.DaySmoke
import com.kapstone.mannersmoker.model.data.smoke.DaySmokeWithCalendar
import com.kapstone.mannersmoker.model.data.smoke.Smoke
import java.util.*

class CalendarPagerAdapter(private val context : Context) : PagerAdapter() {

    companion object {
        const val NUMBER_OF_PAGES = 12 * 10 // 10년 달력
    }

    private var viewContainer: ViewGroup? = null
    private var onDaySmokeClickListener: ((Calendar, Calendar) -> Unit)? = null

    fun setOnDayClickListener(listener: ((Calendar, Calendar) -> Unit)) {
        this.onDaySmokeClickListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setList(list: List<Smoke>) {
        val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
           ((views.getChildAt(i) as GridView).adapter as? CalendarGridAdapter)?.updateList(list)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val gridView = GridView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        gridView.apply {
            overScrollMode = GridView.OVER_SCROLL_NEVER
            layoutParams = params
            numColumns = 7
            adapter = CalendarGridAdapter(context, getCalendar(position))
            setSelector(R.drawable.calendar_list_selector)
            setOnItemClickListener { adapterView, _, pos, _ ->
                val calendar_clicked = adapterView.getItemAtPosition(pos) as DaySmokeWithCalendar // Calendar, List<DaySmoke>
                // 외부에서 setOnDayClickListener 함수를 이용해서 로직을 정의하면 여기서 호출됨
               // onDaySmokeClickListener?.invoke(getCalendar(position), calendar_clicked)
            }
        }

        container.addView(gridView)
        viewContainer = container
        Log.d("adapterr", "여기 실행됨. 뷰 컨테이너 : ${viewContainer!!.size}")
        return gridView
    }

    override fun getCount(): Int = NUMBER_OF_PAGES

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any) : Boolean = (view == `object`)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    private fun getCalendar(position: Int): Calendar {
        return Calendar.getInstance().apply {
            add(Calendar.MONTH, position - NUMBER_OF_PAGES / 2)
        }
    }
}