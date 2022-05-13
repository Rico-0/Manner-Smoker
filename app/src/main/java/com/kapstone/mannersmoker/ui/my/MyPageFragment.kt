package com.kapstone.mannersmoker.ui.my

import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentMypageBinding
import com.kapstone.mannersmoker.model.data.User
import com.kapstone.mannersmoker.ui.main.findNavControllerSafely

class MyPageFragment : BaseFragment<FragmentMypageBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_mypage

    override fun initStartView() {
        initClickListener()
    }

    private fun initClickListener() {
        binding.editProfile.setOnClickListener {
            click.run {
                EditProfileFragment.start(
                    fragment = this,
                    argument = EditProfileFragment.Argument(
                        profileImage = "", // User.profileImage
                        profileName = "홍정현" // User.profileName
                    )
                )
            }
        }
        binding.showReward.setOnClickListener {
            Toast.makeText(requireActivity(), "업적 보기", Toast.LENGTH_SHORT).show()
        }
        binding.setting.setOnClickListener {
            this@MyPageFragment.findNavControllerSafely()?.navigate(R.id.action_go_to_setting)
        }
        binding.smokeHistory.setOnClickListener {
            this@MyPageFragment.findNavControllerSafely()?.navigate(R.id.action_go_to_smoke_calendar)
        }
    }
}