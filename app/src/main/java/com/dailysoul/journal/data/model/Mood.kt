package com.dailysoul.journal.data.model

/**
 * Mood – the 5 emotions available on the Mood selection screen.
 * Each carries a stable DB key, a display label, and a Material icon name.
 */
enum class Mood(val key: String, val label: String, val icon: String) {
    JOY("joy", "Joy", "sunny"),
    CALM("calm", "Calm", "air"),
    NEUTRAL("neutral", "Neutral", "radio_button_unchecked"),
    LOW("low", "Low", "water_drop"),
    HEAVY("heavy", "Heavy", "filter_drama");

    companion object {
        fun fromKey(key: String?): Mood? = values().find { it.key == key }
    }
}
