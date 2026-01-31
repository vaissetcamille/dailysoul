package com.dailysoul.journal.data.db

import androidx.room.*
import com.dailysoul.journal.data.model.JournalEntry

/**
 * JournalDao – Room DAO defining all SQL operations for journal_entries.
 *
 * Every method is suspend-aware (run on IO dispatcher via repository).
 * Queries are ordered newest-first for the History screen.
 */
@Dao
interface JournalDao {

    /** Insert a new entry. Returns the auto-generated row ID. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry): Long

    /** Update an existing entry (matched by primary key). */
    @Update
    suspend fun update(entry: JournalEntry)

    /** Delete a single entry by ID. */
    @Delete
    suspend fun delete(entry: JournalEntry)

    /** Fetch a single entry by its primary key. */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getById(id: Int): JournalEntry?

    /** Fetch the entry for today's date (at most one per day by design). */
    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY createdAt DESC LIMIT 1")
    suspend fun getByDate(date: String): JournalEntry?

    /** All entries, newest first (History screen). */
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    suspend fun getAll(): List<JournalEntry>

    /** Entries for a specific month – uses LIKE on the date prefix "yyyy-MM". */
    @Query("SELECT * FROM journal_entries WHERE date LIKE :monthPrefix ORDER BY createdAt DESC")
    suspend fun getByMonth(monthPrefix: String): List<JournalEntry>

    /** Total count – used to decide whether to show empty-state. */
    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun count(): Int
}
