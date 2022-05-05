package com.kapstone.mannersmoker.ui.my

import android.app.Dialog
import android.content.Intent
import android.os.Parcelable
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentEditProfileBinding
import kotlinx.android.parcel.Parcelize
import java.util.*

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    companion object {
        private const val NAV_ID = R.id.action_go_to_edit_profile

        fun start(
            fragment: Fragment,
            argument: Argument,
            navOptions: NavOptions? = null
        ) {
            try { // 다른 프래그먼트에서 start 함수를 호출하면, 해당 프래그먼트에 정의된 이동 경로를 찾아 이동
                fragment.findNavController().navigate(
                    // bundleOf로 넘어온 값을 저장할 수 있다. Argument 타입은 밑에 있음
                    NAV_ID, bundleOf("argument" to argument), navOptions
                )
            } catch (e: Exception) {

            }
        }
    }

    @Parcelize
    data class Argument ( // DetailFragment가 전달받을 데이터
       val profileImage : String,
       val profileName : String
    ) : Parcelable

    private val argument: Argument by lazy {
        arguments?.getParcelable<Argument>("argument") // ScreenSlidePagerAdapter에서 bundleOf에 argument라는 키로 값 저장한 것 불러오기
            ?: throw IllegalArgumentException("Argument must exist")
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_edit_profile

    override fun initStartView() {
        binding.profileName.text = argument.profileName
    }

}