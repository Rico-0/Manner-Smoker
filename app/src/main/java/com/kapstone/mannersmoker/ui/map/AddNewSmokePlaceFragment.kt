package com.kapstone.mannersmoker.ui.map

import android.content.Context
import androidx.activity.OnBackPressedCallback
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseFragment
import com.kapstone.mannersmoker.databinding.FragmentAddNewSmokePlaceBinding
import com.kapstone.mannersmoker.ui.main.findNavControllerSafely

class AddNewSmokePlaceFragment : BaseFragment<FragmentAddNewSmokePlaceBinding>() {

    private lateinit var callback: OnBackPressedCallback

    override val layoutResourceId: Int
        get() = R.layout.fragment_add_new_smoke_place

    override fun initStartView() {
        binding.addNewSmokePlaceCancel.setOnClickListener {
            findNavControllerSafely()?.navigate(R.id.action_go_to_main)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               findNavControllerSafely()?.navigate(R.id.action_go_to_main)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

}


