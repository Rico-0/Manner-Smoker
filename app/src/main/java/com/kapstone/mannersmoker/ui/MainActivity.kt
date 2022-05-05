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
        PermissionUtil.checkForgroundLocationPermission(this)
        PermissionUtil.checkBackgroundLocationPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 위치 권한 요청을 했고 요청한 퍼미션 개수만큼 수신되었다면
        if (requestCode == PermissionUtil.LOCATION_PERMISSIONS_REQUEST_CODE && grantResults.size == PermissionUtil.REQUIRED_PREMISSIONS.size) {
            var check_result: Boolean = true

            // 모든 퍼미션 허용했는지 체크
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    return@forEach
                }
            }
            if (check_result) {
                Log.d("@@@", "start")
                // 위치 값 가져오기 가능
            } else {
                // 거부한 퍼미션이 있는 경우 앱 사용이 불가능
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        PermissionUtil.REQUIRED_PREMISSIONS[0]
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        PermissionUtil.REQUIRED_PREMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this,
                        "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                   finish()
                } else {
                    Toast.makeText(
                        this,
                        "권한이 거부되었습니다. 설정(앱 정보) 에서 권한을 허용해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PermissionUtil.GPS_ENABLE_REQUEST_CODE -> {
                if (PermissionUtil.checkLocationServicesStatus(this)) {
                    Log.d("@@@", "onActivityResult : GPS 활성화됨")
                    PermissionUtil.checkForgroundLocationPermission(this)
                    return
                } else {
                    PermissionUtil.showDialogForLocationServiceSetting(this)
                }
            }
            PermissionUtil.BACKGROUND_PERMISSION_REQUEST_CODE -> {

            }
        }
    }
}