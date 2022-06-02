package com.kapstone.mannersmoker.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kapstone.mannersmoker.model.db.dao.Repository
import com.kapstone.mannersmoker.viewmodel.MainViewModel

class MainViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}