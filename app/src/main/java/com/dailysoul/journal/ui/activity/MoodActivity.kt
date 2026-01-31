package com.dailysoul.journal.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dailysoul.journal.R
import com.dailysoul.journal.data.model.Mood
import com.dailysoul.journal.util.AppPrefs
import com.dailysoul.journal.databinding.ActivityMoodBinding

/**
 * MoodActivity – optional mood-selection screen shown before journaling.
 *
 * Flow:
 *   1. Render 5 mood buttons (Joy, Calm, Neutral, Low, Heavy).
 *   2. Tapping a mood highlights it and enables "Continue to Journal".
 *   3. "Continue" returns RESULT_OK with the mood key as an Intent extra.
 *   4. "Skip for now" returns RESULT_OK with no mood extra.
 *   5. X (close) returns RESULT_CANCELED.
 */
class MoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodBinding
    private var selectedMood: Mood? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPrefs.isDarkMode(this)) setTheme(R.style.Theme_DailySoul_Dark)
        super.onCreate(savedInstanceState)

        binding = ActivityMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMoodButtons()
        setupActions()
    }

    private fun setupMoodButtons() {
        // Each mood button click: select it and update visuals
        binding.btnMoodJoy.setOnClickListener { selectMood(Mood.JOY) }
        binding.btnMoodCalm.setOnClickListener { selectMood(Mood.CALM) }
        binding.btnMoodNeutral.setOnClickListener { selectMood(Mood.NEUTRAL) }
        binding.btnMoodLow.setOnClickListener { selectMood(Mood.LOW) }
        binding.btnMoodHeavy.setOnClickListener { selectMood(Mood.HEAVY) }
    }

    /**
     * Select a mood: store it, update all button backgrounds,
     * and enable the Continue button.
     */
    private fun selectMood(mood: Mood) {
        selectedMood = mood

        // Reset all buttons to default
        val allButtons = listOf(
            binding.btnMoodJoy,
            binding.btnMoodCalm,
            binding.btnMoodNeutral,
            binding.btnMoodLow,
            binding.btnMoodHeavy
        )
        val moodOrder = listOf(Mood.JOY, Mood.CALM, Mood.NEUTRAL, Mood.LOW, Mood.HEAVY)
        val labelViews = listOf(
            binding.tvMoodJoy,
            binding.tvMoodCalm,
            binding.tvMoodNeutral,
            binding.tvMoodLow,
            binding.tvMoodHeavy
        )

        allButtons.forEachIndexed { index, button ->
            if (moodOrder[index] == mood) {
                button.setBackgroundResource(R.drawable.mood_button_selected)
                labelViews[index].setTextColor(android.graphics.Color.parseColor("#C36A4D"))
                labelViews[index].typeface = android.graphics.Typeface.create(
                    "sans-serif", android.graphics.Typeface.BOLD
                )
            } else {
                button.setBackgroundResource(R.drawable.mood_button_default)
                labelViews[index].setTextColor(android.graphics.Color.parseColor("#8E8A84"))
                labelViews[index].typeface = android.graphics.Typeface.create(
                    "sans-serif", android.graphics.Typeface.NORMAL
                )
            }
        }

        // Enable the Continue button
        binding.btnContinue.isEnabled = true
        binding.btnContinue.setBackgroundResource(R.drawable.btn_primary)
    }

    private fun setupActions() {
        // Close (X) → cancel
        binding.btnClose.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // Continue → return selected mood
        binding.btnContinue.setOnClickListener {
            val resultIntent = android.content.Intent()
            selectedMood?.let {
                resultIntent.putExtra(MainActivity.EXTRA_MOOD, it.key)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Skip → return OK without a mood
        binding.btnSkip.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
