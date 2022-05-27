package com.kapstone.mannersmoker.ui.main

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kapstone.mannersmoker.ui.main.MainActivity.Companion.PAGE_MY
import com.kapstone.mannersmoker.ui.my.MyPageFragment

class ViewPagerAdapter(
    private val fragments: Array<Fragment>, // 화면 Fragment 배열
    fa: FragmentManager, // FragmentStateAdapter 상속 시 필요
    lifecycle: Lifecycle
  //  private val userName : String,
  //  private val userProfileImage : String?
    // 여기에 HomeFragment에서 쓸 당일 흡연량 필요할 듯
) : FragmentStateAdapter(fa, lifecycle){

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        // TODO : if (position == PAGE_HOME) ...
      /*  if (position == PAGE_MY) {
            return fragments[position].apply {
                arguments = bundleOf(
                    "argument" to MyPageFragment.Argument(
                        profileName = userName,
                        profileImage = userProfileImage
                    )
                )
            }
        } */
        return fragments[position]
    }
}