package com.nelsonxilv.gstoutimetable.domain.entity

enum class DayOfWeek(val rawValue: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7),
    UNKNOWN(0);

    companion object {
        fun fromInt(value: Int?) = entries.find { it.rawValue == value } ?: UNKNOWN
    }
}