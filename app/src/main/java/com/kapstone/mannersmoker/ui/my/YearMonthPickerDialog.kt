package com.kapstone.mannersmoker.ui.my

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import com.kapstone.mannersmoker.R

class YearMonthPickerDialog (context : Context){
    private val dialog = Dialog(context)
    private lateinit var year : NumberPicker
    private lateinit var month : NumberPicker
    private lateinit var okButton : TextView
    private lateinit var cancelButton : TextView

    private lateinit var listener : AcceptBtnClickListener

    companion object {
        private const val MAX_YEAR = 2099
        private const val MIN_YEAR = 1980
        private const val MAX_MONTH = 12
        private const val MIN_MONTH = 1
    }

    interface AcceptBtnClickListener {
        fun onClicked()
    }

    fun setAcceptBtnClickListener(listener: (Int, Int) -> Unit) {
        this.listener = object : AcceptBtnClickListener  {
            override fun onClicked() {
                listener.invoke(year.value, month.value)
            }
        }
    }

    fun setDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_set_year_month_for_graph)
        dialog.setCancelable(true)

        year = dialog.findViewById(R.id.year_numberpicker)
        month = dialog.findViewById(R.id.month_numberpicker)
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
        year.apply {
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS // EditText 설정 해제
            minValue = MIN_YEAR
            maxValue = MAX_YEAR
        }
        month.apply {
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS // EditText 설정 해제
            minValue = MIN_MONTH
            maxValue = MAX_MONTH
        }
    }
}