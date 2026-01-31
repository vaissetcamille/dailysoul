package com.dailysoul.journal.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BootReceiver – listens for BOOT_COMPLETED to re-schedule the daily
 * reminder alarm (alarms don't survive reboots by default).
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (AppPrefs.isNotificationsOn(context)) {
            ReminderManager.schedule(
                context,
                AppPrefs.getReminderHour(context),
                AppPrefs.getReminderMinute(context)
            )
        }
    }
}
