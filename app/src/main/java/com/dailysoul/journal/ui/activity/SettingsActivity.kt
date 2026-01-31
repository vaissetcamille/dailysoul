package com.dailysoul.journal.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dailysoul.journal.R
import com.dailysoul.journal.ui.viewmodel.SettingsViewModel
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.util.ExportUtils
import com.dailysoul.journal.util.ReminderManager
import com.dailysoul.journal.data.db.JournalDatabase
import com.dailysoul.journal.databinding.ActivitySettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SettingsActivity – app settings screen.
 *
 * Sections:
 *   Appearance  – Dark Mode toggle (recreates activity on change)
 *   Notifications – Daily Reminder toggle (schedules/cancels alarm)
 *   Data & Privacy – Export Journal (share sheet), Privacy Policy link
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPrefs.isDarkMode(this)) setTheme(R.style.Theme_DailySoul_Dark)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        viewModel.loadPrefs(this)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Back
        binding.btnBack.setOnClickListener { finish() }

        // Dark Mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setDarkMode(this, isChecked)
            recreate()  // re-apply theme
        }

        // Notifications toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setNotificationsOn(this, isChecked)
            if (isChecked) {
                ReminderManager.schedule(
                    this,
                    AppPrefs.getReminderHour(this),
                    AppPrefs.getReminderMinute(this)
                )
            } else {
                ReminderManager.cancel(this)
            }
        }

        // Export
        binding.btnExport.setOnClickListener { exportJournal() }

        // Privacy Policy
        binding.btnPrivacy.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://dailysouljournal.com/privacy")
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.darkMode.observe(this) { isDark ->
            binding.switchDarkMode.isChecked = isDark
        }
        viewModel.notificationsOn.observe(this) { isOn ->
            binding.switchNotifications.isChecked = isOn
        }
    }

    private fun exportJournal() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = JournalDatabase.getInstance(this@SettingsActivity)
            val entries = db.journalDao().getAll()
            val text = ExportUtils.entriesToText(entries)

            (this@SettingsActivity).runOnUiThread {
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Soul Journal – Export")
                }
                startActivity(android.content.Intent.createChooser(intent, "Export your journal"))
            }
        }
    }
}
