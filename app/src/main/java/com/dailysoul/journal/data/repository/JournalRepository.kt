package com.dailysoul.journal.data.repository

import com.dailysoul.journal.data.db.JournalDao
import com.dailysoul.journal.data.model.JournalEntry

/**
 * JournalRepository – the single source of truth between the UI and the database.
 *
 * All methods are suspend functions; callers (ViewModels) launch them
 * inside viewModelScope with Dispatchers.IO.
 *
 * This layer exists so that ViewModels never talk directly to Room,
 * making the code testable and the data layer swappable.
 */
class JournalRepository(private val dao: JournalDao) {

    /** Persist a new entry (auto-save from the Home editor). */
    suspend fun save(entry: JournalEntry): Long = dao.insert(entry)

    /** Overwrite an existing entry (edit flow). */
    suspend fun update(entry: JournalEntry) = dao.update(entry)

    /** Remove an entry permanently. */
    suspend fun delete(entry: JournalEntry) = dao.delete(entry)

    /** Load a single entry by ID (Read screen). */
    suspend fun getById(id: Int): JournalEntry? = dao.getById(id)

    /** Load today's entry if one exists. */
    suspend fun getTodayEntry(todayDate: String): JournalEntry? = dao.getByDate(todayDate)

    /** Load the full history list (newest first). */
    suspend fun getAll(): List<JournalEntry> = dao.getAll()

    /** Load entries for a specific month. monthPrefix example: "2023-09" */
    suspend fun getByMonth(monthPrefix: String): List<JournalEntry> = dao.getByMonth("$monthPrefix%")

    /** Entry count – 0 means show empty state. */
    suspend fun count(): Int = dao.count()
}
