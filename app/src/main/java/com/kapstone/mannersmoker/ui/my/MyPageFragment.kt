package com.kapstone.mannersmoker.ui.my

import android.content.DialogInterface
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentMypageBinding
import com.kapstone.mannersmoker.ui.main.LoginSelectActivity
import com.kapstone.mannersmoker.ui.my.calendar.SmokeCalendarActivity
import com.kapstone.mannersmoker.util.PreferencesManager.login_type
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image
import kotlinx.android.parcel.Parcelize

class MyPageFragment : BaseFragment<FragmentMypageBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_mypage

    override fun initStartView() {
        initClickListener()
        binding.profileName.text = user_id
        if (login_type.equals("kakao")) { // 카카오 로그인인 경우
            binding.loginTypeIcon.setImageResource(R.drawable.kakaotalk_image)
            binding.loginTypeText.text = resources.getString(R.string.kakao_login)
        }
        Glide.with(requireContext())
            .load(user_profile_image)
            .error(R.drawable.my) // 에러 발생 시 기본 이미지
            .into(binding.proflieImage)
    }

    private fun initClickListener() {
        binding.editProfile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 (카카오, 네이버 분기 처리)
        binding.logout.setOnClickListener {
            if (login_type.equals("kakao")) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), "로그아웃 실패 : $error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, LoginSelectActivity::class.java)
                        // 스택 중간에 있었던 액티비티들을 지우는 역할
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        requireActivity().finish()
                    }
                }
            } // else (login_type.equals("naver")) { ...
        }

        // 회원 탈퇴 (카카오, 네이버 분기 처리)
        binding.deleteAccount.setOnClickListener {
            val alterDialog = AlertDialog.Builder(requireActivity())
                .setMessage("회원 탈퇴 시 모든 데이터 복구가 불가능합니다.\n 정말 회원 탈퇴를 진행하시겠습니까?")
                .setPositiveButton("예", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (login_type.equals("kakao")) {
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        requireActivity(),
                                        "회원 탈퇴 실패 $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    login_type = "null"
                                    dialog?.dismiss()
                                    Toast.makeText(
                                        requireActivity(),
                                        "정상적으로 회원 탈퇴가 완료되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(activity, LoginSelectActivity::class.java)
                                    // 스택 중간에 있었던 액티비티들을 지우는 역할
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("아니오", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.let {
                            it.dismiss()
                        }
                    }
                })
            alterDialog.show()
        }

        binding.showReward.setOnClickListener {
            Toast.makeText(requireActivity(), "업적 보기", Toast.LENGTH_SHORT).show()
        }
        binding.setting.setOnClickListener {
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.smokeHistory.setOnClickListener {
            val intent = Intent(requireActivity(), SmokeCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}