package com.kapstone.mannersmoker.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import java.security.Permission

object PermissionUtil {

    const val GPS_ENABLE_REQUEST_CODE = 2001
    const val LOCATION_PERMISSIONS_REQUEST_CODE = 100
    const val BACKGROUND_PERMISSION_REQUEST_CODE = 200

    val REQUIRED_PREMISSIONS = arrayOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun checkLocationServicesStatus(activity : Activity) : Boolean {
        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun checkBackgroundLocationPermission(activity: Activity) {
        // 권한이 있는지 체크
        val hasBackgroundPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        // API 23 이상 버전에서만 background 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (hasBackgroundPermission == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨
               // Log.d(TAG, "백그라운드 위치 권한 허용됨")

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                  //  Log.d(TAG, "백그라운드 위치 권한 허용 안 됨")
                    permissionDialog(activity) // 다이얼로그 출력
                }
            }
        }
    }

    fun checkForgroundLocationPermission(activity: Activity) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 위치 값 가져오기 가능, 권한 허용됨
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    PermissionUtil.REQUIRED_PREMISSIONS[0]
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    PermissionUtil.REQUIRED_PREMISSIONS[1]
                )
            ) {
                Toast.makeText(
                    activity,
                    "앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                ActivityCompat.requestPermissions(
                    activity,
                    PermissionUtil.REQUIRED_PREMISSIONS,
                    PermissionUtil.LOCATION_PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우 퍼미션 요청 바로 하기
                ActivityCompat.requestPermissions(
                    activity,
                    PermissionUtil.REQUIRED_PREMISSIONS,
                    PermissionUtil.LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    // GPS 활성화 다이얼로그 띄우기
    fun showDialogForLocationServiceSetting(activity: Activity) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("위치 서비스 비활성화됨")
        builder.setMessage("앱을 사용하려면 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val callGPSSettingIntent =
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivityForResult(
                    callGPSSettingIntent,
                    PermissionUtil.GPS_ENABLE_REQUEST_CODE
                )
            }
        })
        builder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.cancel()
            }
        })
        builder.create().show()
    }

    // 안드로이드 API 30 버전부터는 backgroundPermission 을 직접 설정해야함
    private fun backgroundPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ), PermissionUtil.BACKGROUND_PERMISSION_REQUEST_CODE
        )
    }

    // 백그라운드 권한 요청
    private fun permissionDialog(activity : Activity) {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE ->
                    backgroundPermission(activity)
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)

        builder.show()
    }
}