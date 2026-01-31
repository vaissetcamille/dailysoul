package com.dailysoul.journal.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailysoul.journal.data.model.JournalEntry

/**
 * JournalDatabase – the single Room database instance for the app.
 *
 * version = 1  (initial schema)
 * Accessed via the thread-safe singleton pattern (getInstance).
 *
 * Migration path: when schema changes happen, add Migration objects here.
 * Using destructiveMigration as a fallback ensures the app never crashes
 * on a schema mismatch – data is cleared only as a last resort.
 */
@Database(entities = [JournalEntry::class], version = 1, exportSchema = true)
abstract class JournalDatabase : RoomDatabase() {

    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabase? = null

        fun getInstance(context: android.content.Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "daily_soul_journal.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
