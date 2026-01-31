package com.dailysoul.journal.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.dailysoul.journal.data.model.JournalEntry
import com.dailysoul.journal.util.DateUtils
import com.dailysoul.journal.databinding.ItemHistoryEntryBinding

/**
 * HistoryAdapter – RecyclerView adapter for the History list.
 *
 * Each item shows:
 *   - Day number (large, italic serif) + day-of-week label (e.g. "THU")
 *   - Time + entry type label (e.g. "7:30 AM • Reflection")
 *   - Preview: first 2 lines of the entry content, faded out at the bottom
 *   - Chevron icon on the right
 *
 * Uses DiffUtil for efficient list updates.
 * The onItemClick callback passes the entry ID back to the Activity.
 */
class HistoryAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<JournalEntry, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<JournalEntry>() {
            override fun areItemsTheSame(old: JournalEntry, new: JournalEntry) = old.id == new.id
            override fun areContentsTheSame(old: JournalEntry, new: JournalEntry) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        holder.bind(entry)
    }

    class ViewHolder(private val binding: ItemHistoryEntryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: JournalEntry) {
            // Day number (e.g. "14")
            binding.tvDayNumber.text = DateUtils.dayNumber(entry.date).toString().padStart(2, '0')

            // Day label (e.g. "THU")
            binding.tvDayLabel.text = DateUtils.dayLabel(entry.date)

            // Time + type
            val time = DateUtils.timeString(entry.createdAt)
            val type = when (entry.mood) {
                "joy"     -> "Reflection"
                "calm"    -> "Reflection"
                "neutral" -> "Insight"
                "low"     -> "Evening Soul"
                "heavy"   -> "Release"
                else      -> "Reflection"
            }
            binding.tvTimeMeta.text = "$time • $type"

            // Preview text (first ~100 chars)
            binding.tvPreview.text = entry.content.take(120)

            // Click handler
            binding.root.setOnClickListener { onItemClick(entry.id) }
        }
    }
}
