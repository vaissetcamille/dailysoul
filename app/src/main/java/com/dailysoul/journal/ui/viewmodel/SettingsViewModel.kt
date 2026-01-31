package com.dailysoul.journal.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dailysoul.journal.util.AppPrefs

/**
 * SettingsViewModel – exposes and mutates app preferences.
 *
 * Each setting is a MutableLiveData<Boolean> so the Activity
 * can observe changes and update toggle switches reactively.
 *
 * The ViewModel does NOT write to SharedPreferences directly;
 * that's the Activity's job after observing the LiveData change.
 * (This keeps the ViewModel testable without a Context.)
 */
class SettingsViewModel : ViewModel() {

    private val _darkMode = MutableLiveData<Boolean>()
    val darkMode: LiveData<Boolean> = _darkMode

    private val _notificationsOn = MutableLiveData<Boolean>()
    val notificationsOn: LiveData<Boolean> = _notificationsOn

    /** Load current prefs into LiveData (call once after Activity has a Context) */
    fun loadPrefs(ctx: android.content.Context) {
        _darkMode.postValue(AppPrefs.isDarkMode(ctx))
        _notificationsOn.postValue(AppPrefs.isNotificationsOn(ctx))
    }

    fun setDarkMode(value: Boolean) { _darkMode.postValue(value) }
    fun setNotificationsOn(value: Boolean) { _notificationsOn.postValue(value) }
}
