package com.kapstone.mannersmoker.ui.my

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import com.kapstone.mannersmoker.R

class EditProfileDialog (context: Context) {

    private val dialog = Dialog(context)
    private lateinit var profileImage : ImageView
    private lateinit var profileName : TextView
    private lateinit var okButton : TextView
    private lateinit var cancelButton : TextView

    private lateinit var listener : AcceptBtnClickListener

    interface AcceptBtnClickListener {
        fun onClicked()
    }

    // TODO : 이미지 url?, 프로필 이름
    fun setAcceptBtnClickListener(listener: (String, String) -> Unit) {
        this.listener = object : AcceptBtnClickListener  {
            override fun onClicked() {
               // listener.invoke(profileImage, profileName)
            }
        }
    }

    fun setDialog() {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.edit_profile_layout)
        dialog.setCancelable(true)

        profileImage = dialog.findViewById(R.id.proflie_image)
        okButton = dialog.findViewById(R.id.dialog_bt_accept)
        cancelButton = dialog.findViewById(R.id.dialog_bt_cancel)

        initNumberPicker()

        okButton.setOnClickListener {
            listener.onClicked()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initNumberPicker() {
        smokeCount.apply {
            wrapSelectorWheel = false // 순환 안되게 막기
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS // EditText 설정 해제
            minValue = 0
            maxValue = 20
        }
    }
}