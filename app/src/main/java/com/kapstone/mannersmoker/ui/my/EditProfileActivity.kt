package com.kapstone.mannersmoker.ui.my

import android.os.Bundle
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityEditProfileBinding

class EditProfileActivity : BaseActivity2<ActivityEditProfileBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_edit_profile

    override fun initStartView() {
        binding.profileName.text = intent.getStringExtra("profileName")
        Glide.with(this)
            .load(intent.getStringExtra("profileImage"))
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