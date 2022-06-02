package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.viewpager.widget.PagerAdapter
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.model.data.DaySmoke
import com.kapstone.mannersmoker.model.data.Smoke
import java.util.*

class CalendarPagerAdapter(private val context : Context) : PagerAdapter() {

    companion object {
        const val NUMBER_OF_PAGES = 12 * 10 // 10년 달력
    }

    private var viewContainer: ViewGroup? = null
    private var onDaySmokeClickListener: ((Calendar, DaySmoke) -> Unit)? = null

    fun setOnDayClickListener(listener: ((Calendar, DaySmoke) -> Unit)) {
        this.onDaySmokeClickListener = listener
    }

    fun setList(list: List<Smoke>) {
        val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
           ((views.getChildAt(i) as GridView).adapter as? CalendarGridAdapter)?.updateList(list)
        }
    }

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
                val selectedDay = adapterView.getItemAtPosition(pos) as DaySmoke // Calendar, List<oneSmoke>
                // 외부에서 setOnDayClickListener 함수를 이용해서 로직을 정의하면 여기서 호출됨
                onDaySmokeClickListener?.invoke(getCalendar(position), selectedDay)
            }
        }

        container.addView(gridView)
        viewContainer = container

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