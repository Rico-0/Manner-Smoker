package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivitySmokeHistoryBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.smoke.*
import com.kapstone.mannersmoker.model.data.smoke.DaySmokeList.smokeList
import com.kapstone.mannersmoker.ui.my.YearMonthPickerDialog
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SmokeCalendarActivity : BaseActivity2<ActivitySmokeHistoryBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_smoke_history

    private val entries = ArrayList<BarEntry>()

    var flag : Boolean = false // true : CalendarView, false : GraphView

    private val smokeDao = RetrofitInstance.smokeDao

    val getAllSmokeData = smokeDao.getAllSmokeData(user_id_from_server).enqueue(object : Callback<SmokeDataClass> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResponse(
            call: Call<SmokeDataClass>,
            response: Response<SmokeDataClass>
        ) {
            val allSmokeData : SmokeDataClass? = response.body()
            Log.d(TAG, "smokedata : ${allSmokeData?.httpStatus}")
            Log.d(TAG, "smokedata : ${Arrays.toString(allSmokeData?.SmokeData?.toTypedArray())}")
            allSmokeData?.SmokeData?.let {
               // binding.customCalendarView.notifyDataChanged(it)
            }
        }

        override fun onFailure(call: Call<SmokeDataClass>, t: Throwable) {
            Toast.makeText(this@SmokeCalendarActivity, "흡연량 데이터를 받아오는 데 실패하였습니다. $t", Toast.LENGTH_SHORT).show()
        }

    })

    override fun initStartView() {
        initClickListener()
        val cal = Calendar.getInstance()
        // 첫 화면에는 현재 달의 데이터 셋팅
        setBarChart(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        binding.selectDateBarChart.setOnClickListener {
            showDialog()
        }
        initCalendar()

    }

    override fun onResume() {
        super.onResume()
        getAllSmokeData
    }

    private fun initCalendar() {
        binding.smokeCalendarView.addDecorators(Decorator.TodayDecorator(), Decorator.SundayDecorator(), Decorator.SaturdayDecorator())
        binding.smokeCalendarView.setOnDateChangedListener { widget, date, selected ->
            val intent =
                Intent(this@SmokeCalendarActivity, DailySmokeDetailActivity::class.java).apply {
                    putExtra("year", date.year)
                    putExtra("month", date.month + 1)
                    putExtra("date", date.day)
                }
            startActivity(intent)
        }
    }

    private fun setDailySmokeDataToBarEntry(list : List<Smoke>) : HashMap<Int, List<Smoke>> {
        val result = HashMap<Int, List<Smoke>>()
        for (element in list) {
            val tempList = ArrayList<Smoke>()
            val oneDay = element.day
            for (element2 in list) {
                if (element2.day == oneDay){
                    tempList.add(element2)
                }
            }
            result.put(oneDay, tempList)
        }
        return result
    }

    private fun showDialog() {
        val dialog = YearMonthPickerDialog(this)
        dialog.setAcceptBtnClickListener { year, month ->
            setBarChart(year, month)
        }
        dialog.setDialog()
    }

    private fun generateDays(lastDay : Int) : ArrayList<String> {
        val result = ArrayList<String>()
        for (i in 1..lastDay) {
            result.add("$i" + "일")
        }
        return result
    }

    private fun setBarChart(year : Int, month : Int) {
        val lastDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DATE, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
        Log.d(TAG, "차트 날짜 : ${lastDay}")

        val dayList = generateDays(lastDay)
        entries.clear() // 데이터 제거해 줘야 정상적으로 날짜별 그래프 그릴 수 있음

            smokeDao.getMonthSmokeData(user_id_from_server, month, year).enqueue(object : Callback<SmokeDataClass> {
                override fun onResponse(
                    call: Call<SmokeDataClass>,
                    response: Response<SmokeDataClass>
                ) {
                    val smokeData = response.body()
                    smokeData?.SmokeData?.let {
                        Log.d(TAG, "차트 흡연량 값 : $year 년 $month 월 ${smokeData.SmokeData.size}")
                        //  1.2f라는 좌표에, 20.0f만큼의 그래프 영역을 그린다는 의미
                        for (i in 1..lastDay) {
                            entries.add(BarEntry(
                                i.toFloat(),
                                (setDailySmokeDataToBarEntry(smokeData.SmokeData).get(i)?.size ?: 0)?.toFloat()!!)
                            )
                        }
                        binding.barChart.run {
                            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
                            setMaxVisibleValueCount(30) // 최대 보이는 그래프 개수를 7개로 지정
                            setPinchZoom(true) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                            setDrawBarShadow(false) //그래프의 그림자
                            setDrawGridBackground(false) //격자구조 넣을건지
                            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                                axisMinimum = 0f // 최소값 0
                                granularity = 10f // 50 단위마다 선을 그리려고 설정.
                                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                                setDrawGridLines(true) //격자 라인 활용
                                setDrawAxisLine(false) // 축 그리기 설정
                                axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // 축 색깔 설정
                                gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
                                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                                textSize = 13f //라벨 텍스트 크기
                            }
                            xAxis.run {
                                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                                granularity = 0.5f // 1 단위만큼 간격 두기
                                setDrawAxisLine(true) // 축 그림
                                setDrawGridLines(false) // 격자
                                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //라벨 색상
                                textSize = 10f // 텍스트 크기
                                valueFormatter = MyXAxisFormatter(dayList) // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
                            }
                            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
                            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
                            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
                            legend.isEnabled = false //차트 범례 설정
                        }

                        var set = BarDataSet(entries,"DataSet") // 데이터셋 초기화
                        set.color = ContextCompat.getColor(applicationContext!!,R.color.design_default_color_primary_dark) // 바 그래프 색 설정

                        val dataSet :ArrayList<IBarDataSet> = ArrayList()
                        dataSet.add(set)
                        val data = BarData(dataSet)
                        data.barWidth = 0.5f //막대 너비 설정
                        binding.barChart.run {
                            this.data = data //차트의 데이터를 data로 설정해줌.
                            setFitBars(true)
                            invalidate()
                        }
                    }
                }

                override fun onFailure(call: Call<SmokeDataClass>, t: Throwable) {
                    Log.d(TAG, "막대그래프 그리기 실패 : $t")
                }
            })
        }

    private fun initClickListener() {

        binding.fabCalendarFloatingActionButton.setOnClickListener {
            flag = when (flag) {
                false -> true
                true -> false
            }
            binding.flag = flag
        }
    }

    inner class MyXAxisFormatter(private val dayList : ArrayList<String>) : ValueFormatter() {
       override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return dayList.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

}