package com.dailysoul.journal.util

import java.util.Calendar

/**
 * DailyPrompts – provides a different reflective prompt each day.
 *
 * The prompt is selected deterministically from the day-of-year (1-365),
 * so the same day always shows the same prompt – no randomness surprises.
 * The list cycles when exhausted.
 */
object DailyPrompts {

    private val prompts = listOf(
        "What brought you peace today?",
        "What are you grateful for right now?",
        "How did you spend your energy today?",
        "What made you smile today?",
        "What is weighing on your heart?",
        "What did you learn about yourself today?",
        "Where did you find beauty today?",
        "What are you looking forward to?",
        "How are you feeling in this moment?",
        "What would you tell your younger self?",
        "What made today different?",
        "Where did you feel most alive today?",
        "What kindness did you witness or practice?",
        "What are you holding onto, and why?",
        "How did nature speak to you today?",
        "What moment do you want to remember?",
        "What truth revealed itself to you?",
        "How did you show up for yourself today?",
        "What brought you comfort?",
        "What are you ready to release?",
        "Where did you find stillness today?",
        "What conversation nourished your soul?",
        "How did your body feel today?",
        "What small joy did you notice?",
        "What are you curious about right now?",
        "How did you honor your own boundaries today?",
        "What dream are you nurturing?",
        "Where did you feel connected today?",
        "What surprised you today?",
        "How are you growing right now?"
    )

    /** Returns today's prompt based on day of year. */
    fun today(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return prompts[(dayOfYear - 1) % prompts.size]
    }
}
