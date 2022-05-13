package com.kapstone.mannersmoker.ui.my

import android.content.Context
import androidx.activity.OnBackPressedCallback
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentCalendarSmokeHistoryBinding
import com.kapstone.mannersmoker.ui.main.findNavControllerSafely

class SmokeCalendarFragment : BaseFragment<FragmentCalendarSmokeHistoryBinding>() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // go to main fragment
            findNavControllerSafely()?.navigate(R.id.action_go_to_main)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_calendar_smoke_history

    override fun initStartView() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback);
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}