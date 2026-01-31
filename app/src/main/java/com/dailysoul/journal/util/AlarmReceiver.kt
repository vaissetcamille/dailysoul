package com.dailysoul.journal.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dailysoul.journal.R
import com.dailysoul.journal.ui.activity.MainActivity

/**
 * AlarmReceiver – BroadcastReceiver that fires when the daily alarm triggers.
 *
 * Posts a gentle reminder notification that taps back into MainActivity.
 * On Android 8+ (API 26) we create a NotificationChannel if it doesn't exist.
 */
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "daily_soul_reminder"
        const val CHANNEL_NAME = "Daily Reminder"
        const val NOTIFICATION_ID = 42
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Only post if notifications are still enabled in prefs
        if (!AppPrefs.isNotificationsOn(context)) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel on API 26+ (only runs once; subsequent calls are no-ops)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "A gentle nudge to reflect on your day"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // PendingIntent → opens MainActivity when notification is tapped
        val tapIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setTitle("Daily Soul")
            .setMessage("Take a moment to reflect on your day.")
            .setPendingIntent(tapIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
