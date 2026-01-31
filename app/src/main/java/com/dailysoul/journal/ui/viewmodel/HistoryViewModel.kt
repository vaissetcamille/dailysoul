package com.dailysoul.journal.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailysoul.journal.data.model.JournalEntry
import com.dailysoul.journal.data.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * HistoryViewModel – state & logic for the History (list) screen.
 *
 * Responsibilities:
 *   - Load the full list of journal entries (or filter by month)
 *   - Expose the currently active month filter (null = "All Time")
 *   - Provide the list of available month-filter chips
 */
class HistoryViewModel(private val repository: JournalRepository) : ViewModel() {

    /** The full entry list, ordered newest-first */
    private val _entries = MutableLiveData<List<JournalEntry>>()
    val entries: LiveData<List<JournalEntry>> = _entries

    /** Currently selected month filter: null means "All Time" */
    private val _activeFilter = MutableLiveData<String?>()
    val activeFilter: LiveData<String?> = _activeFilter

    /** List of distinct month prefixes present in the DB, for filter chips */
    private val _availableMonths = MutableLiveData<List<String>>()
    val availableMonths: LiveData<List<String>> = _availableMonths

    /** Loading indicator */
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadAll()
    }

    /** Load all entries and derive the available month chips */
    private fun loadAll() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val all = repository.getAll()
            _entries.postValue(all)

            // Extract unique month prefixes (yyyy-MM), preserving order (newest first)
            val months = all.map { it.date.take(7) }.distinct()
            _availableMonths.postValue(months)
            _isLoading.postValue(false)
        }
    }

    /** Apply a month filter (or null to show all) */
    fun filterByMonth(monthPrefix: String?) {
        _activeFilter.postValue(monthPrefix)
        if (monthPrefix == null) {
            loadAll()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.postValue(true)
                _entries.postValue(repository.getByMonth(monthPrefix))
                _isLoading.postValue(false)
            }
        }
    }

    /** Refresh after returning from Read screen (entry may have been edited/deleted) */
    fun refresh() {
        val current = _activeFilter.value
        filterByMonth(current)
    }
}

class HistoryViewModelFactory(private val repository: JournalRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
