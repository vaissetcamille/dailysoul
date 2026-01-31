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
 * ReadViewModel – state & logic for the single-entry reader screen.
 *
 * Receives an entry ID via constructor; loads and exposes the entry.
 * Also handles delete and edit-save operations.
 */
class ReadViewModel(
    private val repository: JournalRepository,
    private val entryId: Int
) : ViewModel() {

    private val _entry = MutableLiveData<JournalEntry?>()
    val entry: LiveData<JournalEntry?> = _entry

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    /** Fires true when the entry has been successfully deleted */
    private val _deletedEvent = MutableLiveData<Boolean>()
    val deletedEvent: LiveData<Boolean> = _deletedEvent

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            _entry.postValue(repository.getById(entryId))
            _isLoading.postValue(false)
        }
    }

    /** Delete the current entry and signal the Activity to finish */
    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            val e = _entry.value ?: return@launch
            repository.delete(e)
            _deletedEvent.postValue(true)
        }
    }

    /** Update the entry's content (from inline edit mode) */
    fun updateContent(newContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val e = _entry.value ?: return@launch
            val updated = e.copy(content = newContent, updatedAt = System.currentTimeMillis())
            repository.update(updated)
            _entry.postValue(updated)
        }
    }
}

class ReadViewModelFactory(
    private val repository: JournalRepository,
    private val entryId: Int
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReadViewModel(repository, entryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
