package com.kapstone.mannersmoker.ui.my

import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityEditProfileBinding
import com.kapstone.mannersmoker.util.PreferencesManager.user_id
import com.kapstone.mannersmoker.util.PreferencesManager.user_profile_image

class EditProfileActivity : BaseActivity2<ActivityEditProfileBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_edit_profile

    override fun initStartView() {
        binding.profileName.text = user_id
        Glide.with(this)
            .load(user_profile_image)
            .error(R.drawable.my) // 에러 발생 시 기본 이미지
            .into(binding.proflieImage)
        binding.changeProfileCancel.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}