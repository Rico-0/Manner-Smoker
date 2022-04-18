package com.kapstone.mannersmoker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity
import com.kapstone.mannersmoker.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


}