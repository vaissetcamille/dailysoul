package com.dailysoul.journal.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dailysoul.journal.R
import com.dailysoul.journal.data.db.JournalDatabase
import com.dailysoul.journal.data.repository.JournalRepository
import com.dailysoul.journal.ui.viewmodel.MainViewModel
import com.dailysoul.journal.ui.viewmodel.MainViewModelFactory
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.util.DateUtils
import com.dailysoul.journal.databinding.ActivityMainBinding

/**
 * MainActivity – the Home / Today journaling screen.
 *
 * Flow:
 *   1. Show today's date + reflective prompt.
 *   2. Load any existing entry for today; if found, pre-fill the textarea.
 *   3. Auto-save with a 2-second debounce as the user types.
 *   4. "Save Soul" button for explicit save.
 *   5. Bottom nav to History and Settings.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val autoSaveHandler = Handler(Looper.getMainLooper())
    private var autoSaveRunnable: Runnable? = null
    private var isInitialLoad = true   // guard to skip first programmatic setText

    companion object {
        const val EXTRA_MOOD = "extra_mood"
        private const val AUTO_SAVE_DELAY_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPrefs.isDarkMode(this)) setTheme(R.style.Theme_DailySoul_Dark)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = JournalDatabase.getInstance(this)
        val repo = JournalRepository(db.journalDao())
        viewModel = ViewModelProvider(this, MainViewModelFactory(repo))[MainViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Check if MoodActivity returned a mood
        val mood = intent?.getStringExtra(EXTRA_MOOD)
        if (mood != null) {
            viewModel.setMood(mood)
            intent.removeExtra(EXTRA_MOOD)
        }
    }

    private fun setupUI() {
        // Header
        binding.tvToday.text = "TODAY"
        binding.tvDate.text = DateUtils.headerString()
        binding.tvPrompt.text = viewModel.prompt

        // Text watcher for auto-save debounce
        binding.etJournalContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isInitialLoad) {
                    isInitialLoad = false
                    return
                }
                scheduleAutoSave()
            }
        })

        // Save Soul button
        binding.btnSaveSoul.setOnClickListener {
            viewModel.saveEntry(binding.etJournalContent.text.toString())
            // Quick scale feedback animation
            binding.btnSaveSoul.animate().scaleX(0.92f).scaleY(0.92f).duration = 80
            binding.btnSaveSoul.animate().scaleX(1.0f).scaleY(1.0f).duration = 80
                .startDelay = 80
        }

        // Bottom nav
        binding.navHistory.setOnClickListener {
            startActivity(android.content.Intent(this, HistoryActivity::class.java))
        }
        binding.navSettings.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
        binding.navJournal.setOnClickListener { /* already on this screen */ }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            if (!loading) {
                val entry = viewModel.currentEntry.value
                if (entry != null) {
                    binding.etJournalContent.setText(entry.content)
                    binding.tvStatus.text = "Saved"
                } else {
                    binding.tvStatus.text = "Drafting..."
                }
            }
        }

        viewModel.savedEvent.observe(this) { saved ->
            if (saved) binding.tvStatus.text = "Saved"
        }
    }

    private fun scheduleAutoSave() {
        binding.tvStatus.text = "Drafting..."
        autoSaveRunnable?.let { autoSaveHandler.removeCallbacks(it) }
        autoSaveRunnable = Runnable {
            viewModel.saveEntry(binding.etJournalContent.text.toString())
        }
        autoSaveHandler.postDelayed(autoSaveRunnable!!, AUTO_SAVE_DELAY_MS)
    }

    override fun onPause() {
        super.onPause()
        // Force-save on pause – nothing lost
        autoSaveRunnable?.let { autoSaveHandler.removeCallbacks(it) }
        viewModel.saveEntry(binding.etJournalContent.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        autoSaveRunnable?.let { autoSaveHandler.removeCallbacks(it) }
    }
}
