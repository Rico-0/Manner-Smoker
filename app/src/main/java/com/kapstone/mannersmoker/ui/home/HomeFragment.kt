package com.kapstone.mannersmoker.ui.home

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentHomeBinding
import com.kapstone.mannersmoker.model.data.DailySmoke
import com.kapstone.mannersmoker.model.data.DailySmokeData
import com.kapstone.mannersmoker.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResourceId: Int
    get() = R.layout.fragment_home

    private val homeViewModel = HomeViewModel()

    private var todaySmokeCount : Int = DailySmokeData.todaySmokeCount

    override fun initStartView() {

    }

    private fun observeData() {
        homeViewModel.currentHealth.observe(this, Observer {
            homeViewModel.updateCurrentHealth(todaySmokeCount)
        })
    }

}