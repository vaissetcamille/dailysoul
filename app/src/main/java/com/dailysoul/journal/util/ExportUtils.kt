package com.dailysoul.journal.util

import com.dailysoul.journal.data.model.JournalEntry

/**
 * ExportUtils – generates a plain-text representation of journal entries
 * suitable for sharing or saving as a .txt file.
 *
 * Format per entry:
 *   ─────────────────────────────
 *   Date: Monday, October 24, 2023
 *   Time: 7:30 AM
 *   Mood: Calm
 *
 *   <content>
 */
object ExportUtils {

    fun entriesToText(entries: List<JournalEntry>): String {
        val sb = StringBuilder()
        sb.appendLine("═══════════════════════════════")
        sb.appendLine("   Daily Soul Journal – Export")
        sb.appendLine("═══════════════════════════════\n")

        entries.forEachIndexed { index, entry ->
            if (index > 0) sb.appendLine("\n───────────────────────────────\n")
            sb.appendLine("Date: ${DateUtils.readDateString(entry.createdAt)}")
            sb.appendLine("Time: ${DateUtils.timeString(entry.createdAt)}")
            entry.mood?.let { mood ->
                val label = com.dailysoul.journal.data.model.Mood.fromKey(mood)?.label ?: mood
                sb.appendLine("Mood: $label")
            }
            sb.appendLine()
            sb.appendLine(entry.content)
        }
        return sb.toString()
    }
}
