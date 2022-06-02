package com.kapstone.mannersmoker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kapstone.mannersmoker.model.data.RetrofitInstance
import com.kapstone.mannersmoker.model.data.SmokeAreaDataClass
import com.kapstone.mannersmoker.model.data.SmokeAreaModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private lateinit var smokeDao: SmokeDao

    fun getAllSmokeArea() {
        smokeDao = RetrofitInstance.smokeDao
        smokeDao.getAllSmokeArea().enqueue(object : Callback<SmokeAreaDataClass> {
            override fun onResponse(
                call: Call<SmokeAreaDataClass>,
                response: Response<SmokeAreaDataClass>
            ) {
                val result: SmokeAreaDataClass? = response.body()
                Log.d("MainViewModel", "data : $result")
                val places = result?.placeData

                Log.d("MainViewModel", "places : ${places?.size}")

                for (i in places?.indices!!) {
                    val latitude: Double = places[i].latitude
                    val longtitude: Double = places[i].longtitude

                   // smokeAreaAllLists.add(SmokeAreaModel(latitude, longtitude))
                }
            }

            override fun onFailure(call: Call<SmokeAreaDataClass>, t: Throwable) {
                Log.d("MainViewModel", "흡연 구역 데이터 받아오기 실패 : $t")
            }
        })
        // TODO : 응답값 유지 안되는 문제 해결
       // Log.d("MainViewModel", "smokeAreaAllLists : ${smokeAreaAllLists.size}")
    }

    /*
    용산구 - 1
    영등포구 - 2
    광진구 - 3
    중구 - 4
    중랑구 - 5
    동작구 - 6
    송파구 - 7
    서대문구 - 8
    동대문구 - 9
     */
    fun getSmokeArea(areaCode: Int) {
        smokeDao = RetrofitInstance.smokeDao
        smokeDao.getSmokeArea(areaCode).enqueue(object : Callback<SmokeAreaDataClass> {
            override fun onResponse(
                call: Call<SmokeAreaDataClass>,
                response: Response<SmokeAreaDataClass>
            ) {
                val result: SmokeAreaDataClass? = response.body()
                val places = result?.let {
                    result.placeData
                }

                if (places != null) {
                    for (i in 0 until places.size) {
                        val latitude: Double = places[i].latitude
                        val longtitude: Double = places[i].longtitude

                     //   smokeAreaOneLists.add(SmokeAreaModel(latitude, longtitude))
                    }
                }
            }

            override fun onFailure(call: Call<SmokeAreaDataClass>, t: Throwable) {
                Log.d("MainViewModel", "흡연 구역 데이터 받아오기 실패 : $t")
            }
        })
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable =
    apply { compositeDisposable.add(this) }