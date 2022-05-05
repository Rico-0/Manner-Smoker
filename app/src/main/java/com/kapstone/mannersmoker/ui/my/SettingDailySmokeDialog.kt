package com.kapstone.mannersmoker.ui.my

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import com.kapstone.mannersmoker.R

class SettingDailySmokeDialog (context: Context) {

    private val dialog = Dialog(context)
    private lateinit var smokeCount : NumberPicker
    private lateinit var okButton : TextView
    private lateinit var cancelButton : TextView

    private lateinit var listener : AcceptBtnClickListener

    interface AcceptBtnClickListener {
        fun onClicked()
    }

    fun setAcceptBtnClickListener(listener: (Int) -> Unit) {
        this.listener = object : AcceptBtnClickListener  {
            override fun onClicked() {
                listener.invoke(smokeCount.value)
            }
        }
    }

    fun setDialog() {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_dailysmoke_numberpicker)
        dialog.setCancelable(true)

        smokeCount = dialog.findViewById(R.id.set_smoke_numberpicker)
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