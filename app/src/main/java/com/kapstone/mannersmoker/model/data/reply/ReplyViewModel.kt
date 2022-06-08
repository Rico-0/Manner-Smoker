package com.kapstone.mannersmoker.model.data.reply

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kapstone.mannersmoker.base.BaseViewModel
import com.kapstone.mannersmoker.model.db.dao.SmokeDao
import com.kapstone.mannersmoker.ui.community.ReplyAdapter
import com.kapstone.mannersmoker.util.PreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplyViewModel(private val smokeDao: SmokeDao) : BaseViewModel() {
    private val _replys = MutableLiveData<List<Reply>>()
    val replys: LiveData<List<Reply>>
        get() = _replys

    fun getAllReplys(postId : Int) {
        viewModelScope.launch {
            smokeDao.getReplys(postId).enqueue(object : Callback<ReplyDataClass> {
                override fun onResponse(
                    call: Call<ReplyDataClass>,
                    response: Response<ReplyDataClass>
                ) {
                    if (response.isSuccessful) {
                        val replys = response.body()
                        replys?.replyData?.let {
                            _replys.postValue(it)
                        }
                    }
                }
                override fun onFailure(call: Call<ReplyDataClass>, t: Throwable) {
                      Log.d(TAG, "댓글 데이터 받아오기 실패")
                }
            })
        }
    }
}