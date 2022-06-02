package com.kapstone.mannersmoker.util

import com.kapstone.mannersmoker.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationListener
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.kapstone.mannersmoker.ui.home.HomeFragment
import com.kapstone.mannersmoker.ui.main.MainActivity
import com.kapstone.mannersmoker.ui.my.MyPageFragment
import com.kapstone.mannersmoker.util.DateUtil.stringToDate
import com.kapstone.mannersmoker.util.PreferencesManager.notification_smoke
import com.kapstone.mannersmoker.util.PreferencesManager.time_last_smoke
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Ketan Ramani on 05/11/18.
 */
class BackgroundLocationUpdateService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /* Declare in manifest
    <service android:name=".BackgroundLocationUpdateService"/>
    */
    private val TAG_LOCATION = "TAG_LOCATION"
    private var context: Context? = null
    private var stopService = false

    /* For Google Fused API */
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var latitude = "0.0"
    private var longitude = "0.0"
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null

    /* For Google Fused API */
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    if (!stopService) {
                        //Perform your task here
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (!stopService) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(10))
                    }
                }
            }
        }
        handler.postDelayed(runnable, 2000)
        buildGoogleApiClient()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.e("BackgroundService", "Service Stopped")
        stopService = true
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            Log.e(TAG_LOCATION, "Location Update Callback Removed")
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // TODO : 알림 클릭하면 앱 강제 종료되는 문제 해결 ...
    private fun StartForeground() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("currentSmoke", true)
        val pendingIntent = PendingIntent.getActivity ( // 알림 클릭을 가능하게 함
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT // Extra data만 변경
        )
        val CHANNEL_ID = "background_channel_id"
        val CHANNEL_NAME = "background_channel_name"
        var builder: NotificationCompat.Builder?
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오부터는 알림을 채널에 등록해야 함
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            builder.setChannelId(CHANNEL_ID)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
        } else {
            builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        }
        builder.setContentTitle("흡연 여부 체크")
        builder.setContentText("현재 흡연 중이신가요? 흡연 중이시라면 이 알림을 클릭하세요.")
        val notificationSound =
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(notificationSound)
        builder.setAutoCancel(true)
        builder.setOngoing(false)
        builder.setSmallIcon(R.drawable.smoking_place)
        builder.setContentIntent(pendingIntent)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 잠금화면에 컨텐츠 노출
        val notification: Notification = builder.build()
        startForeground(101, notification)
        notification.flags = Notification.FLAG_AUTO_CANCEL // 알림 클릭 시 사라짐
    }

    // 위치가 변경된 경우 호출됨
    override fun onLocationChanged(location: Location) {
      /*  Log.d(TAG_LOCATION, "time_last_smoke : $time_last_smoke")
        if (time_last_smoke != null) { // 예전에 흡연한 기록이 있을 경우
            val date = Date() // 현재 시간
            val timeDifference =
                (date.time - stringToDate(time_last_smoke!!).time) / 60000 // 분 차이 계산
            // 등록되어 있는 흡연 구역 위치들 중 변경된 위치와 200m 내에 있는 흡연 구역이 있는 경우 알림 발송
            if (timeDifference > 10) { // 10분이 지났을 경우 알림
                notification_smoke = true
                for (i in 0 until Places.places.size) {
                    val place = Places.places.get(i)
                    val distance = LocationDistance.distance(
                        location.latitude,
                        location.longitude,
                        place.latitude,
                        place.longitude,
                        "meter"
                    )
                    if (distance >= 200 && notification_smoke) {
                        StartForeground()
                        notification_smoke = !notification_smoke
                        return
                    }
                }
            }
        } else {
            for (i in 0 until Places.places.size) {
                val place = Places.places.get(i)
                val distance = LocationDistance.distance(
                    location.latitude,
                    location.longitude,
                    place.latitude,
                    place.longitude,
                    "meter"
                )
                if (distance >= 200) {
                    StartForeground()
                    return
                }
            }
        }
        Log.e(
            TAG_LOCATION,
            "Location Changed Latitude : " + location.latitude + "\tLongitude : " + location.longitude
        )
        latitude = location.latitude.toString()
        longitude = location.longitude.toString()
        if (latitude.equals("0.0", ignoreCase = true) && longitude.equals(
                "0.0",
                ignoreCase = true
            )
        ) {
            requestLocationUpdate()
        } else {
            Log.e(
                TAG_LOCATION,
                "Latitude : " + location.latitude + "\tLongitude : " + location.longitude
            )
        } */
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()?.apply {
            setInterval(10 * 1000)
            setFastestInterval(5 * 1000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient
            ?.checkLocationSettings(mLocationSettingsRequest)
            ?.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
                override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                    Log.e(TAG_LOCATION, "GPS Success")
                    requestLocationUpdate()
                }
            })?.addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    val statusCode: Int = (e as ApiException).getStatusCode()
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val REQUEST_CHECK_SETTINGS = 214
                            val rae: ResolvableApiException = e as ResolvableApiException
                            rae.startResolutionForResult(
                                context as AppCompatActivity?,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: SendIntentException) {
                            Log.e(TAG_LOCATION, "Unable to execute request.")
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                            TAG_LOCATION,
                            "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                        )
                    }
                }
            })
    }

    override fun onConnectionSuspended(i: Int) {
        connectGoogleClient()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        buildGoogleApiClient()
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        mSettingsClient = LocationServices.getSettingsClient(context!!)
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        connectGoogleClient()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.e(TAG_LOCATION, "Location Received")
                mCurrentLocation = locationResult.getLastLocation()
                onLocationChanged(mCurrentLocation!!)
            }
        }
    }

    private fun connectGoogleClient() {
        val googleAPI: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode: Int = googleAPI.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient?.connect()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }
}