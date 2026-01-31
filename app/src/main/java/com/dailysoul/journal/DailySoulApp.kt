package com.dailysoul.journal

import android.app.Application
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.util.ReminderManager

/**
 * DailySoulApp – custom Application subclass.
 *
 * On first launch:
 *   - Schedules the daily reminder alarm if notifications are enabled.
 *
 * This is the single entry-point for app-wide initialization.
 */
class DailySoulApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Re-arm the daily reminder alarm (survives app kill, not reboot – BootReceiver handles that)
        if (AppPrefs.isNotificationsOn(this)) {
            ReminderManager.schedule(
                this,
                AppPrefs.getReminderHour(this),
                AppPrefs.getReminderMinute(this)
            )
        }
    }
}
