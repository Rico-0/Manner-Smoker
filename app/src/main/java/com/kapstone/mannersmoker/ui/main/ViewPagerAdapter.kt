package com.kapstone.mannersmoker.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val fragments: Array<Fragment>, // 화면 Fragment 배열
    fa: FragmentManager, // FragmentStateAdapter 상속 시 필요
    lifecycle: Lifecycle
    // 여기에 HomeFragment에서 쓸 당일 흡연량 필요할 듯
) : FragmentStateAdapter(fa, lifecycle){
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        // TODO : if (position == PAGE_HOME) ...
        return fragments[position]
    }
}