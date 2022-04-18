package com.kapstone.mannersmoker.ui.map

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import com.kapstone.mannersmoker.R

class MapDialog(context: Context) {

    private val dialog = Dialog(context)
    private lateinit var goToKakaoMap : TextView
    private lateinit var cancelFind : TextView

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
        dialog.setContentView(R.layout.ballon_layout)
        dialog.setCancelable(true)

        goToKakaoMap = dialog.findViewById(R.id.dialog_bt_accept)
        cancelFind = dialog.findViewById(R.id.dialog_bt_cancel)

        goToKakaoMap.setOnClickListener {
            listener.onClicked()
            dialog.dismiss()
        }

        cancelFind.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}