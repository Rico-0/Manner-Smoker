package com.kapstone.mannersmoker.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionUtil {

    const val GPS_ENABLE_REQUEST_CODE = 2001
    const val PERMISSIONS_REQUEST_CODE = 100
    val REQUIRED_PREMISSIONS = arrayOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun checkRunTimePermission(activity : Activity) {
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
            if (shouldShowRequestPermissionRationale(
                    activity,
                    REQUIRED_PREMISSIONS[0]
                ) ||
                shouldShowRequestPermissionRationale(
                    activity,
                    REQUIRED_PREMISSIONS[1]
                )
            ) {
                Toast.makeText(activity, "앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.requestPermissions(
                    activity,
                    REQUIRED_PREMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우 퍼미션 요청 바로 하기
                ActivityCompat.requestPermissions(
                    activity,
                    REQUIRED_PREMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    fun checkLocationServicesStatus(activity : Activity) : Boolean {
        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}