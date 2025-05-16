package com.example.aos.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun strToDate(date: String?): String? {
        val fmtDate = when (date) {
            "2 Weeks" -> Date().minusDays(14).yymmdd()
            "1 Month" -> Date().minusMonths(1).yymmdd()
            "2 Months" -> Date().minusMonths(2).yymmdd()
            "6 Months" -> Date().minusMonths(6).yymmdd()
            "1 Year" -> Date().minusYears(1).yymmdd()
            else -> null
        }
        return fmtDate
    }

    private fun Date.minusDays(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }

    private fun Date.minusMonths(months: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MONTH, -months)
        return calendar.time
    }

    private fun Date.minusYears(years: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.YEAR, -years)
        return calendar.time
    }

    private fun Date.yymmdd(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(this)
    }
} 