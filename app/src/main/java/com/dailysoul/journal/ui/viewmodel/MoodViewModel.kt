package com.dailysoul.journal.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dailysoul.journal.data.model.Mood

/**
 * MoodViewModel – state for the Mood selection screen.
 *
 * Holds the currently selected Mood (or null = none selected yet).
 * The Activity reads this and passes the result back via Intent extra.
 */
class MoodViewModel : ViewModel() {
    private val _selectedMood = MutableLiveData<Mood?>()
    val selectedMood: LiveData<Mood?> = _selectedMood

    fun select(mood: Mood) {
        _selectedMood.postValue(mood)
    }
}
