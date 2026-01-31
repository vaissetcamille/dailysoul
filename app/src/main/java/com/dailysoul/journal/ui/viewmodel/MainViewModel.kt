package com.dailysoul.journal.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailysoul.journal.data.model.JournalEntry
import com.dailysoul.journal.data.repository.JournalRepository
import com.dailysoul.journal.util.DateUtils
import com.dailysoul.journal.util.DailyPrompts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * MainViewModel – state & logic for the Home (Today) screen.
 *
 * Responsibilities:
 *   - Expose today's prompt
 *   - Load any existing entry for today (resume editing)
 *   - Auto-save the entry as the user types (debounced in Activity)
 *   - Track character count for the status bar
 */
class MainViewModel(private val repository: JournalRepository) : ViewModel() {

    /** The reflective prompt displayed at the top of the page */
    val prompt: String = DailyPrompts.today()

    /** Today's date in ISO format – shared with the UI for display */
    val todayDate: String = DateUtils.todayIso()

    /** The current entry being edited (null if brand new) */
    private val _currentEntry = MutableLiveData<JournalEntry?>()
    val currentEntry: LiveData<JournalEntry?> = _currentEntry

    /** True while we're loading the existing entry from the DB */
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    /** Success feedback flag after save */
    private val _savedEvent = MutableLiveData<Boolean>()
    val savedEvent: LiveData<Boolean> = _savedEvent

    /** The selected mood (set from MoodActivity result) */
    private val _selectedMood = MutableLiveData<String?>()
    val selectedMood: LiveData<String?> = _selectedMood

    init {
        loadTodayEntry()
    }

    /** Check if today already has a saved entry; if so, restore it */
    private fun loadTodayEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getTodayEntry(todayDate)
            _currentEntry.postValue(existing)
            _isLoading.postValue(false)
        }
    }

    /** Set the mood chosen on the Mood screen */
    fun setMood(mood: String?) {
        _selectedMood.postValue(mood)
    }

    /**
     * Save (or update) today's entry.
     * Called by the Activity on "Save Soul" button press or on auto-save timer.
     */
    fun saveEntry(content: String) {
        if (content.isBlank()) return   // Don't persist empty entries

        viewModelScope.launch(Dispatchers.IO) {
            val existing = _currentEntry.value
            if (existing != null) {
                // Update the existing row
                val updated = existing.copy(
                    content = content,
                    mood = _selectedMood.value ?: existing.mood,
                    updatedAt = System.currentTimeMillis()
                )
                repository.update(updated)
                _currentEntry.postValue(updated)
            } else {
                // Insert a new row
                val newEntry = JournalEntry(
                    date = todayDate,
                    mood = _selectedMood.value,
                    content = content,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                val id = repository.save(newEntry)
                _currentEntry.postValue(newEntry.copy(id = id.toInt()))
            }
            _savedEvent.postValue(true)
        }
    }
}

/**
 * Factory for MainViewModel – required because it takes a JournalRepository parameter.
 * Android's ViewModelProvider needs a Factory to instantiate ViewModels with dependencies.
 */
class MainViewModelFactory(private val repository: JournalRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
