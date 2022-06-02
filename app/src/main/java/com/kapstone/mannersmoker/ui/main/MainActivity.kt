package com.kapstone.mannersmoker.ui.main

import com.kapstone.mannersmoker.ui.map.MapFragment
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityMainBinding
import com.kapstone.mannersmoker.model.db.dao.Repository
import com.kapstone.mannersmoker.ui.community.CommunityFragment
import com.kapstone.mannersmoker.ui.home.HomeFragment
import com.kapstone.mannersmoker.ui.my.MyPageFragment
import com.kapstone.mannersmoker.ui.news.NewsFragment
import com.kapstone.mannersmoker.util.BackgroundLocationUpdateService
import com.kapstone.mannersmoker.util.DateUtil
import com.kapstone.mannersmoker.util.NetworkConnection
import com.kapstone.mannersmoker.util.PreferencesManager
import com.kapstone.mannersmoker.util.PreferencesManager.is_setted_first_smoke_time
import com.kapstone.mannersmoker.util.PreferencesManager.login_type
import com.kapstone.mannersmoker.util.PreferencesManager.time_last_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.time_start_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.today_smoke_amount
import com.kapstone.mannersmoker.util.PreferencesManager.used_money
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import com.kapstone.mannersmoker.viewmodel.MainViewModel
import com.kapstone.mannersmoker.viewmodelfactory.MainViewModelFactory
import kotlinx.android.synthetic.main.custom_tab_button.view.*
import java.util.*

class MainActivity : BaseActivity2<ActivityMainBinding>() {

    private var loginType: String = ""

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private var isCurrentSmoke: Boolean = false

    val mainViewModel = MainViewModel()

    companion object {
        val TAB_LAYOUT_TEXT = arrayOf("Map", "Community", "Home", "News", "My")
    }

    override fun initStartView() {
        checkNetworkConnection()
       // startService(Intent(this, BackgroundLocationUpdateService::class.java))
        // 카카오톡으로 로그인 시도한 경우
        loginType = intent.getStringExtra("loginType") ?: "null"
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
        )
        binding.mainViewPager.offscreenPageLimit = fragments.size
        binding.mainViewPager.adapter = viewPagerAdapter
        binding.mainViewPager.isUserInputEnabled = false

        initTabLayout()

        if (!is_setted_first_smoke_time) {
            showDialog()
        }

        // "현재 흡연중이신가요?" 알림 클릭했을 시 넘어오는 intent 데이터로 Home화면 Data 변경
        Log.d(TAG, "isCurrentSmoke : ${intent.getBooleanExtra("currentSmoke", false)}")
        isCurrentSmoke = intent.getBooleanExtra("currentSmoke", false)
        if (isCurrentSmoke) {
            today_smoke_amount += 1
            used_money += 225 // 1개비 당 약 225원
            val date = Date()
            time_last_smoke = DateUtil.dateToString(date)
        }
    }

    private fun showDialog() {
        val dialog = SetFirstSmokeTimeDialog(this)
        dialog.setAcceptBtnClickListener {
            setDatePickerDialog()
        }
        dialog.setDialog()
    }

    private fun setDatePickerDialog() {
        // 최초 앱 실행 시 흡연 시작일 설정
        var dateString = ""
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                time_start_smoke = dateString
                is_setted_first_smoke_time = true
            }
        DatePickerDialog(
            this,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
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

    private fun checkNetworkConnection() {
        // 네트워크 연결 여부 체크
        val connection = NetworkConnection(applicationContext)
        connection.observe(this, { isConnected ->
            if (!isConnected) {
                Toast.makeText(
                    this,
                    "인터넷에 연결되어 있지 않아 현재 위치를 받아올 수 없습니다.\n 와이파이나 LTE에 연결해 주세요.",
                    Toast.LENGTH_LONG
                ).show()
                return@observe
            }
        })
    }

}