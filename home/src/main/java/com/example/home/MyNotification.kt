package com.example.mynotepad

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

object MyNotification {
    private const val NOTIFICATION_ID = 1

    fun doNotify(context: Context, content:String) {
        // sub noti
//        val contentIntent = Intent(
//            context,
//            PaperWeightActivity::class.java
//        )
//        val contentPendingIntent = PendingIntent.getActivity(
//            context,
//            NOTIFICATION_ID,
//            contentIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id)
        )
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(content)
        builder.setAutoCancel(true)
//        builder.setFullScreenIntent(contentPendingIntent, true)

        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setDefaults(Notification.DEFAULT_ALL)
        val snoozeIntent = Intent(context, AlarmReceiver::class.java)
        snoozeIntent.action = "snooze"
        snoozeIntent.putExtra("noti_id", 0)
        val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0)
        builder.addAction(R.drawable.ic_launcher_background, "snooze", snoozePendingIntent)
        val notificationManager: NotificationManager =
            context.getSystemService<NotificationManager>(
                NotificationManager::class.java
            )
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}