package com.dailysoul.journal.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * DateUtils – centralized date formatting helpers used across the app.
 *
 * All formatters share the default Locale to avoid locale-sensitive bugs.
 */
object DateUtils {
    private val locale = Locale.getDefault()

    /** "yyyy-MM-dd" – the canonical date key stored in the database */
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", locale)

    /** "EEEE, MMMM dd" – e.g. "Monday, October 24" for the Home header */
    private val headerFormat = SimpleDateFormat("EEEE, MMMM dd", locale)

    /** "EEEE, MMMM dd, yyyy" – e.g. "Tuesday, October 24, 2023" for the Read screen */
    private val readFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", locale)

    /** "yyyy-MM" – month prefix for filtering entries by month */
    private val monthFormat = SimpleDateFormat("yyyy-MM", locale)

    /** "h:mm a" – e.g. "7:30 AM" for the history list time column */
    private val timeFormat = SimpleDateFormat("h:mm a", locale)

    /** "MMMM" – just the month name, e.g. "September" */
    private val monthNameFormat = SimpleDateFormat("MMMM", locale)

    /** "MMMM yyyy" – e.g. "September 2023" for history section headers */
    private val sectionFormat = SimpleDateFormat("MMMM yyyy", locale)

    /** Today's date as ISO string */
    fun todayIso(): String = isoFormat.format(Date())

    /** Home header: "Monday, October 24" */
    fun headerString(date: Date = Date()): String = headerFormat.format(date)

    /** Read screen full date */
    fun readDateString(epochMillis: Long): String = readFormat.format(Date(epochMillis))

    /** Month prefix for DB queries */
    fun monthPrefix(date: Date = Date()): String = monthFormat.format(date)

    /** Time string from epoch */
    fun timeString(epochMillis: Long): String = timeFormat.format(Date(epochMillis))

    /** Section header from ISO date string */
    fun sectionHeader(isoDate: String): String {
        val date = isoFormat.parse(isoDate) ?: return isoDate
        return sectionFormat.format(date)
    }

    /** Month name only from ISO date */
    fun monthName(isoDate: String): String {
        val date = isoFormat.parse(isoDate) ?: return isoDate
        return monthNameFormat.format(date)
    }

    /** Parse ISO date string back to Date */
    fun parseIso(isoDate: String): Date? = isoFormat.parse(isoDate)

    /** Day-of-week short label e.g. "THU" */
    fun dayLabel(isoDate: String): String {
        val date = isoFormat.parse(isoDate) ?: return ""
        return SimpleDateFormat("EEE", locale).format(date).uppercase()
    }

    /** Day number (1-31) from ISO date */
    fun dayNumber(isoDate: String): Int {
        val cal = Calendar.getInstance()
        cal.time = isoFormat.parse(isoDate) ?: return 0
        return cal.get(Calendar.DAY_OF_MONTH)
    }
}
