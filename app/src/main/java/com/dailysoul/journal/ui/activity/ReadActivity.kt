package com.dailysoul.journal.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dailysoul.journal.R
import com.dailysoul.journal.data.db.JournalDatabase
import com.dailysoul.journal.data.repository.JournalRepository
import com.dailysoul.journal.ui.viewmodel.ReadViewModel
import com.dailysoul.journal.ui.viewmodel.ReadViewModelFactory
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.util.DateUtils
import com.dailysoul.journal.databinding.ActivityReadBinding

/**
 * ReadActivity – full-screen reader for a single journal entry.
 *
 * Features:
 *   - Centered serif typography for immersive reading
 *   - Floating bottom toolbar: Edit / Delete / Share
 *   - Edit mode: swaps to EditText, saves on back/finish
 *   - Delete: confirmation dialog → finish
 *   - Share: system share sheet with plain text
 */
class ReadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadBinding
    private lateinit var viewModel: ReadViewModel
    private var isEditing = false

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPrefs.isDarkMode(this)) setTheme(R.style.Theme_DailySoul_Dark)
        super.onCreate(savedInstanceState)

        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryId = intent.getIntExtra(EXTRA_ENTRY_ID, -1)
        if (entryId == -1) { finish(); return }

        val db = JournalDatabase.getInstance(this)
        val repo = JournalRepository(db.journalDao())
        viewModel = ViewModelProvider(this, ReadViewModelFactory(repo, entryId))[ReadViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Edit button (LinearLayout wrapper)
        binding.btnEdit.setOnClickListener { toggleEditMode() }

        // Delete button
        binding.btnDelete.setOnClickListener { confirmDelete() }

        // Share button
        binding.btnShare.setOnClickListener { shareEntry() }
    }

    private fun observeViewModel() {
        viewModel.entry.observe(this) { entry ->
            if (entry != null) {
                binding.tvDate.text = DateUtils.readDateString(entry.createdAt).uppercase()
                binding.tvContent.text = entry.content
                binding.etContent.setText(entry.content)
            }
        }

        viewModel.deletedEvent.observe(this) { deleted ->
            if (deleted) finish()
        }
    }

    // ── Edit Mode Toggle ──────────────────────────────────

    private fun toggleEditMode() {
        isEditing = !isEditing
        if (isEditing) {
            // Show EditText, hide read-only text
            binding.tvContent.visibility = android.view.View.GONE
            binding.etContent.visibility = android.view.View.VISIBLE
            binding.etContent.requestFocus()
        } else {
            // Save and switch back to read-only
            val newContent = binding.etContent.text.toString()
            viewModel.updateContent(newContent)
            binding.tvContent.visibility = android.view.View.VISIBLE
            binding.etContent.visibility = android.view.View.GONE
        }
    }

    // ── Delete Confirmation ───────────────────────────────

    private fun confirmDelete() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.read_delete_title)
            .setMessage(R.string.read_delete_message)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Share ─────────────────────────────────────────────

    private fun shareEntry() {
        val entry = viewModel.entry.value ?: return
        val shareText = "${DateUtils.readDateString(entry.createdAt)}\n\n${entry.content}"
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Soul – Journal Entry")
        }
        startActivity(android.content.Intent.createChooser(intent, "Share your reflection"))
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (isEditing) {
            toggleEditMode()  // save and exit edit mode first
        } else {
            super.onBackPressed()
        }
    }
}
