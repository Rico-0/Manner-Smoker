package com.kapstone.mannersmoker.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentMainBinding
import com.kapstone.mannersmoker.ui.community.CommunityFragment
import com.kapstone.mannersmoker.ui.home.HomeFragment
import com.kapstone.mannersmoker.ui.map.MapFragment
import com.kapstone.mannersmoker.ui.my.MyPageFragment
import com.kapstone.mannersmoker.ui.news.NewsFragment
import kotlinx.android.synthetic.main.custom_tab_button.view.*

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private lateinit var callback: OnBackPressedCallback // 뒤로 가기 버튼 눌렸을 시 로직 구현하기 위해 사용

    companion object {
        var WAIT_TIME = 0L
        val TAB_LAYOUT_TEXT = arrayOf("Map", "Community", "Home", "News", "My")
        const val PAGE_MAP = 0
        const val PAGE_COMMUNITY = 1
        const val PAGE_HOME = 2
        const val PAGE_NEWS = 3
        const val PAGE_MY = 4
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_main


    override fun initStartView() {
        val fragments = arrayOf<Fragment>(
            MapFragment(),
            CommunityFragment(),
            HomeFragment(),
            NewsFragment(),
            MyPageFragment()
        )

        val viewPagerAdapter = ViewPagerAdapter(
            fragments,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.mainViewPager.offscreenPageLimit = fragments.size
        binding.mainViewPager.adapter = viewPagerAdapter
        binding.mainViewPager.isUserInputEnabled = false

        initTabLayout()

    }

    private fun createTabLayoutView(tabName : String) : View {
        var tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_button, null)
        tabView.tab_text.text = tabName
        when (tabName) {
            "Map" -> {
                tabView.tab_image.setImageResource(R.drawable.location)
                return tabView
            }
            "Community" -> {
                tabView.tab_image.setImageResource(R.drawable.community)
                return tabView
            }
            "Home" -> {
                tabView.tab_image.setImageResource(R.drawable.home)
                return tabView
            }
            "News" -> {
                tabView.tab_image.setImageResource(R.drawable.news)
                return tabView
            }
            "My" -> {
                tabView.tab_image.setImageResource(R.drawable.my)
                return tabView
            } else -> {
                return tabView
            }
        }
    }

    private fun initTabLayout() {
        TabLayoutMediator(binding.mainTablayout, binding.mainViewPager) { tab, position ->
            var tabText = TAB_LAYOUT_TEXT.get(position)
            tab.customView = createTabLayoutView(tabText)
        }.attach()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(System.currentTimeMillis() - WAIT_TIME >= 1500 ) {
                    WAIT_TIME = System.currentTimeMillis()
                    Toast.makeText(requireActivity(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
                } else {

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        callback.remove()
    }


}