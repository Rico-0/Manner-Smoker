package com.kapstone.mannersmoker.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityMainBinding
import com.kapstone.mannersmoker.ui.community.CommunityFragment
import com.kapstone.mannersmoker.ui.home.HomeFragment
import com.kapstone.mannersmoker.ui.map.MapFragment
import com.kapstone.mannersmoker.ui.my.MyPageFragment
import com.kapstone.mannersmoker.ui.news.NewsFragment
import com.kapstone.mannersmoker.util.PermissionUtil
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.isForegroundPermissionChecked
import com.kapstone.mannersmoker.util.PreferencesManager.login_type
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import kotlinx.android.synthetic.main.custom_tab_button.view.*

class MainActivity : BaseActivity2<ActivityMainBinding>() {

    private val loginType: String = ""

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    companion object {
        val TAB_LAYOUT_TEXT = arrayOf("Map", "Community", "Home", "News", "My")
        const val PAGE_MAP = 0
        const val PAGE_COMMUNITY = 1
        const val PAGE_HOME = 2
        const val PAGE_NEWS = 3
        const val PAGE_MY = 4
    }

    override fun initStartView() {

            // 카카오톡으로 로그인 시도한 경우
            Log.d(TAG, "initStartView()")
            if (loginType.equals("kakao")) {
                login_type = "kakao"
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("MainActivity", "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        user_id = user.kakaoAccount?.profile?.nickname!!
                        user_profile_image = user.kakaoAccount?.profile?.thumbnailImageUrl
                        Log.d(TAG, user_id + ", " + user_profile_image)
                    } else if (user == null) { // 로그인 기록이 없는 경우
                        Toast.makeText(this, "유저 정보를 받아오는 데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val fragments = arrayOf<Fragment>(
                MapFragment(),
                CommunityFragment(),
                HomeFragment(),
                NewsFragment(),
                MyPageFragment()
            )

            val viewPagerAdapter = ViewPagerAdapter(
                fragments,
                supportFragmentManager,
                lifecycle
                // user_id!!,
                // user_profile_image
            )
            binding.mainViewPager.offscreenPageLimit = fragments.size
            binding.mainViewPager.adapter = viewPagerAdapter
            binding.mainViewPager.isUserInputEnabled = false

            initTabLayout()
        }

    private fun createTabLayoutView(tabName: String): View {
        var tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab_button, null)
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
            }
            else -> {
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
}