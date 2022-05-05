package com.kapstone.mannersmoker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.ui.MainActivity
import com.kapstone.mannersmoker.util.PreferencesManager.alarm_daily_smoke

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val DAILY_SMOKE_NOTIFICATION_ID = 0
        const val NEAR_TO_SMOKE_PLACE_NOTIFICATION_ID = 1
        // 안드로이드 오레오 이전까지는 채널 생성을 하지 않아도 Notification을 띄울 수 있었지만 이후부턴 Channel을 생성한 뒤 Channel ID를 부여해야 한다.
        // 앱에 대해 단 한번만 생성하면 되고 이후 재호출 해도 같은 Paramerter에 대해 어떠한 동작을 하지 않으므로 재호출해도 된다.
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        deliverDailySmokeNotification(context)
    }

    private fun deliverDailySmokeNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java) // 알림 클릭 시 MainActivity가 호출됨
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            DAILY_SMOKE_NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.smoking_place)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("오늘의 일일 흡연량을 설정하고 금연에 한 걸음 더 가까워져 보세요!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (alarm_daily_smoke)
            notificationManager.notify(DAILY_SMOKE_NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}