package com.dailysoul.journal.util

import android.content.Context
import android.content.SharedPreferences

/**
 * AppPrefs – thin wrapper around SharedPreferences for app settings.
 *
 * Keys stored:
 *   dark_mode          – Boolean  (theme toggle)
 *   notifications_on   – Boolean  (daily reminder on/off)
 *   reminder_hour      – Int      (0-23, default 20 = 8 PM)
 *   reminder_minute    – Int      (0-59, default 0)
 *   show_mood_screen   – Boolean  (whether to show mood before journaling)
 */
object AppPrefs {
    private const val PREF_FILE = "daily_soul_prefs"

    // ── Keys ──
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_NOTIFICATIONS_ON = "notifications_on"
    const val KEY_REMINDER_HOUR = "reminder_hour"
    const val KEY_REMINDER_MINUTE = "reminder_minute"
    const val KEY_SHOW_MOOD = "show_mood_screen"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    // ── Getters ──
    fun isDarkMode(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_DARK_MODE, false)
    fun isNotificationsOn(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_NOTIFICATIONS_ON, true)
    fun getReminderHour(ctx: Context): Int = prefs(ctx).getInt(KEY_REMINDER_HOUR, 20)
    fun getReminderMinute(ctx: Context): Int = prefs(ctx).getInt(KEY_REMINDER_MINUTE, 0)
    fun isShowMood(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_SHOW_MOOD, true)

    // ── Setters ──
    fun setDarkMode(ctx: Context, value: Boolean) {
        prefs(ctx).edit { putBoolean(KEY_DARK_MODE, value) }
    }
    fun setNotificationsOn(ctx: Context, value: Boolean) {
        prefs(ctx).edit { putBoolean(KEY_NOTIFICATIONS_ON, value) }
    }
    fun setReminderHour(ctx: Context, value: Int) {
        prefs(ctx).edit { putInt(KEY_REMINDER_HOUR, value) }
    }
    fun setReminderMinute(ctx: Context, value: Int) {
        prefs(ctx).edit { putInt(KEY_REMINDER_MINUTE, value) }
    }
    fun setShowMood(ctx: Context, value: Boolean) {
        prefs(ctx).edit { putBoolean(KEY_SHOW_MOOD, value) }
    }
}
