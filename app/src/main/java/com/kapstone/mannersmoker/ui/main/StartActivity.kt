package com.kapstone.mannersmoker.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.util.PermissionUtil
import com.kapstone.mannersmoker.util.PreferencesManager.isForegroundPermissionChecked

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (!isForegroundPermissionChecked) {
            PermissionUtil.checkForgroundLocationPermission(this)
        } else {
            val intent = Intent(this, LoginSelectActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var check_result: Boolean = true
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                check_result = false
                return@forEach
            }
        }
        // 위치 권한 요청을 했고 요청한 퍼미션 개수만큼 수신되었다면
        if (requestCode == PermissionUtil.LOCATION_PERMISSIONS_REQUEST_CODE && grantResults.size == PermissionUtil.REQUIRED_PREMISSIONS.size) {
            if (check_result) {
                isForegroundPermissionChecked = true
                val intent = Intent(this, LoginSelectActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
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
        }
    }
}