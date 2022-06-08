package com.kapstone.mannersmoker.ui.my.calendar

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityDailySmokeDetailBinding
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.smoke.SmokeDataClass
import com.kapstone.mannersmoker.util.PreferencesManager.user_id_from_server
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailySmokeDetailActivity : BaseActivity2<ActivityDailySmokeDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_daily_smoke_detail

    private var smokeDao = RetrofitInstance.smokeDao

    private lateinit var dailySmokeAdapter : DailySmokeAdapter

    private var year = -1
    private var month = -1
    private var day = -1

    override fun initStartView() {
        getDate()
        smokeDao.getDaySmokeData(user_id_from_server, day, month, year).enqueue(object : Callback<SmokeDataClass> {
            override fun onResponse(
                call: Call<SmokeDataClass>,
                response: Response<SmokeDataClass>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.SmokeData?.let {
                        val linearLayoutManager = LinearLayoutManager(
                            this@DailySmokeDetailActivity,
                            LinearLayoutManager.VERTICAL,
                            true
                        )
                        binding.smokeCount.text = "${data.SmokeData.size}개비"
                        binding.smokeIcon.setImageDrawable(setSmokeIcon(data.SmokeData.size))
                        binding.percentText.text = setSmokeText(data.SmokeData.size)
                        dailySmokeAdapter = DailySmokeAdapter(this@DailySmokeDetailActivity, data.SmokeData)
                        runOnUiThread {
                            binding.dailySmokeListView.apply {
                                layoutManager = linearLayoutManager
                                setHasFixedSize(false)
                                setItemViewCacheSize(10)
                                adapter = dailySmokeAdapter
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SmokeDataClass>, t: Throwable) {
                Log.d(TAG, "흡연량 데이터 받아오기 실패")
            }
        })
    }

    private fun getDate() {
        year = intent.getIntExtra("year", -1)
        month = intent.getIntExtra("month", -1)
        day = intent.getIntExtra("date", -1)
        Log.d(TAG, "year : $year , month : $month , day : $day")
    }

    private fun setSmokeIcon(smoke: Int): Drawable? {
        when (smoke) {
            0 -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_icon)
            }
            in 1..4 -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_red5)
            }
            in 5..8 -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_red4)
            }
            in 9..12 -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_red3)
            }
            in 13..16 -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_red2)
            }
            else -> {
                return ContextCompat.getDrawable(this, R.drawable.smoke_red)
            }
        }
    }

    private fun setSmokeText(smoke: Int) : String {
        when (smoke) {
            0 -> {
                return "(데이터 없음)"
            }
            in 1..4 -> {
                return "(최상)"
            }
            in 5..8 -> {
                return "(좋음)"
            }
            in 9..12 -> {
                return "(보통)"
            }
            in 13..16 -> {
                return "(나쁨)"
            }
            else -> {
                return "(최악)"
            }
        }
    }
}