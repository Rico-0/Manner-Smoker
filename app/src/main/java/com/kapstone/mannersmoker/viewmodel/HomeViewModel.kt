package com.kapstone.mannersmoker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kapstone.mannersmoker.base.BaseViewModel
import com.kapstone.mannersmoker.model.data.HomeData
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val _currentHealth = MutableLiveData<Int>()
    val currentHealth : LiveData<Int>
        get() = _currentHealth
    private val _timeFromLastSmoke = MutableLiveData<String>()
    val timeFromLastSmoke : LiveData<String>
        get() = _timeFromLastSmoke
    private val _usedMoney = MutableLiveData<Int>()
    val usedMoney : LiveData<Int>
        get() = _usedMoney
    private val _todaySmokeAmount = MutableLiveData<Int>()
    val todaySmokeAmount : LiveData<Int>
    get() = _todaySmokeAmount

    fun updateCurrentHealth(healthScore : Int) {
        _currentHealth.postValue(healthScore)
    }
}

