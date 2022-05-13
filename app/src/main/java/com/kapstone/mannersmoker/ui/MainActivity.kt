package com.kapstone.mannersmoker.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity
import com.kapstone.mannersmoker.databinding.ActivityMainBinding
import com.kapstone.mannersmoker.ui.my.SettingFragment
import com.kapstone.mannersmoker.util.PermissionUtil

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}