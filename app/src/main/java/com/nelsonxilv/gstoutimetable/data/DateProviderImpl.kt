package com.nelsonxilv.gstoutimetable.data

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import com.nelsonxilv.gstoutimetable.domain.DateProvider
import com.nelsonxilv.gstoutimetable.domain.DateType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

class DateProviderImpl @Inject constructor() : DateProvider {

    override fun getDayOfWeekNumber(dateType: DateType): Int {
        val dayOfWeek = getLocalDate(dateType).dayOfWeek
        val ruLocale = Locale.Builder()
            .setLanguage(LANGUAGE)
            .setRegion(COUNTRY)
            .build()

        return dayOfWeek.get(WeekFields.of(ruLocale).dayOfWeek())
    }

    override fun getFormattedDate(dateType: DateType): String {
        val calendar = Calendar.getInstance().apply {
            if (dateType == DateType.TOMORROW)
                add(Calendar.DAY_OF_YEAR, TOMORROW_DATE_AMOUNT)
        }
        val dateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault())

        return dateFormat.format(calendar.time)
    }

    override fun getWeekNumber(dateType: DateType): Int {
        val date = getLocalDate(dateType)

        val september16thYear =
            if (date.monthValue >= Month.SEPTEMBER.value && date.dayOfMonth >= LESSONS_START_DAY) {
                date.year
            } else {
                date.year - 1
            }

        val september16th = LocalDate.of(
            september16thYear,
            Month.SEPTEMBER,
            LESSONS_START_DAY
        )
        val daysDifference = date.toEpochDay() - september16th.toEpochDay()
        val weekNumber = (daysDifference / 7) + 1

        return if (weekNumber % 2 == 0L) SECOND_WEEK else FIRST_WEEK
    }

    override fun getAllDaysOfWeek(): Map<Int, String> {
        return DayOfWeek.entries.associate { dayOfWeek ->
            dayOfWeek.value to dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }
    }

    private fun getLocalDate(dateType: DateType): LocalDate {
        return if (dateType == DateType.TOMORROW) {
            LocalDate.now().plusDays(TOMORROW_DATE_AMOUNT.toLong())
        } else {
            LocalDate.now()
        }
    }

    companion object {
        private const val LANGUAGE = "ru"
        private const val COUNTRY = "RU"
        private const val PATTERN_DATE_FORMAT = "d MMMM, EEEE"
        private const val FIRST_WEEK = 1
        private const val SECOND_WEEK = 2
        private const val TOMORROW_DATE_AMOUNT = 1
        private const val LESSONS_START_DAY = 16
    }

}