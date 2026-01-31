package com.dailysoul.journal.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dailysoul.journal.R
import com.dailysoul.journal.data.db.JournalDatabase
import com.dailysoul.journal.data.repository.JournalRepository
import com.dailysoul.journal.ui.adapter.HistoryAdapter
import com.dailysoul.journal.ui.viewmodel.HistoryViewModel
import com.dailysoul.journal.ui.viewmodel.HistoryViewModelFactory
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.databinding.ActivityHistoryBinding

/**
 * HistoryActivity – chronological list of all journal entries.
 *
 * Features:
 *   - Horizontal filter chips (All Time + month names)
 *   - RecyclerView with HistoryAdapter
 *   - Tap → ReadActivity
 *   - FAB (+) → back to MainActivity for new entry
 *   - Refreshes on resume (after edit/delete in ReadActivity)
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPrefs.isDarkMode(this)) setTheme(R.style.Theme_DailySoul_Dark)
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = JournalDatabase.getInstance(this)
        val repo = JournalRepository(db.journalDao())
        viewModel = ViewModelProvider(this, HistoryViewModelFactory(repo))[HistoryViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupUI() {
        // RecyclerView
        adapter = HistoryAdapter { entryId ->
            val intent = android.content.Intent(this, ReadActivity::class.java)
            intent.putExtra(ReadActivity.EXTRA_ENTRY_ID, entryId)
            startActivity(intent)
        }
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter

        // FAB
        binding.fabNewEntry.setOnClickListener {
            startActivity(android.content.Intent(this, MainActivity::class.java))
        }

        // Bottom nav
        binding.navJournal.setOnClickListener {
            startActivity(android.content.Intent(this, MainActivity::class.java))
        }
        binding.navSettings.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.entries.observe(this) { entries ->
            adapter.submitList(entries)
            binding.tvEmptyState.visibility =
                if (entries.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.availableMonths.observe(this) { months ->
            buildFilterChips(months)
        }
    }

    /**
     * Dynamically populate the horizontal chip row.
     * "All Time" is always first; then one chip per available month.
     */
    private fun buildFilterChips(months: List<String>) {
        binding.chipGroup.removeAllViews()

        // "All Time"
        addChip("All Time", isSelected = viewModel.activeFilter.value == null) {
            viewModel.filterByMonth(null)
            buildFilterChips(months)  // refresh chip visuals
        }

        // Month chips
        months.forEach { monthPrefix ->
            val label = com.dailysoul.journal.util.DateUtils.monthName(monthPrefix + "-01")
            val isSelected = viewModel.activeFilter.value == monthPrefix
            addChip(label, isSelected) {
                viewModel.filterByMonth(monthPrefix)
                buildFilterChips(months)
            }
        }
    }

    private fun addChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
        val chip = androidx.appcompat.widget.AppCompatButton(this)
        chip.text = text
        chip.setPadding(32, 0, 32, 0)
        chip.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            72
        )
        chip.setBackgroundResource(if (isSelected) R.drawable.chip_selected else R.drawable.chip_default)
        chip.setTextColor(
            if (isSelected) android.graphics.Color.WHITE
            else android.graphics.Color.parseColor("#4A4542")
        )
        chip.textSize = 13f
        chip.setOnClickListener { onClick() }
        binding.chipGroup.addView(chip)
    }
}
