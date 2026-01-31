package com.dailysoul.journal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * JournalEntry – the core Room entity for every journal entry.
 *
 * Fields:
 *   id        – auto-generated primary key
 *   date      – calendar date "yyyy-MM-dd" (for grouping / filtering)
 *   mood      – nullable mood key ("joy","calm","neutral","low","heavy")
 *   content   – full text written by the user
 *   createdAt – epoch millis (creation timestamp, used for time-of-day display)
 *   updatedAt – epoch millis (refreshed on every edit)
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String = "",
    val mood: String? = null,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
