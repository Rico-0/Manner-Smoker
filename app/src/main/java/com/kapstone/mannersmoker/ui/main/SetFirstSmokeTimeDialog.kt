package com.kapstone.mannersmoker.ui.main

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import com.kapstone.mannersmoker.R

class SetFirstSmokeTimeDialog(context: Context) {

    private val dialog = Dialog(context)
    private lateinit var setDate : TextView
    private lateinit var cancel : TextView

    private lateinit var listener : AcceptBtnClickListener

    interface AcceptBtnClickListener {
        fun onClicked()
    }

    fun setAcceptBtnClickListener(listener: () -> Unit) {
        this.listener = object : AcceptBtnClickListener  {
            override fun onClicked() {
                listener()
            }
        }
    }

    fun setDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_set_first_smoke_time)
        dialog.setCancelable(true)

        setDate = dialog.findViewById(R.id.dialog_bt_accept)
        cancel = dialog.findViewById(R.id.dialog_bt_cancel)

        setDate.setOnClickListener {
            listener.onClicked()
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}