package com.example.common

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.common.R

// Notification ID.
private const val NOTIFICATION_ID = 1

object AlarmNotification {

    var text:String? = null

    fun createNotification(
        context: Context
    ) : Notification { // default noti
//        val contentIntent = Intent(context, PaperWeightActivity::class.java) // noti 눌렀을 때 접근하는 액티비티
//        val contentPendingIntent = PendingIntent.getActivity(
//            context,
//            NOTIFICATION_ID,
//            contentIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )

        val builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id)
        )

        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText("동일 간격 노티 알람 만들기!!")
//        builder.setContentIntent(contentPendingIntent)
//        builder.setAutoCancel(true)
//        builder.setOngoing(true)

        builder.priority = NotificationCompat.PRIORITY_HIGH
        Log.d("kyi123", "made builder.build()")
        return builder.build()
    }
}