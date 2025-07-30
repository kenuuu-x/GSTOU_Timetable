package com.nelsonxilv.gstoutimetable.domain.entity

enum class ActivityType(val rawValue: Int) {
    LECTURE(1),
    LAB(2),
    PRACTICE(3),
    UNKNOWN(0);

    companion object {
        fun fromInt(value: Int) = entries.find { it.rawValue == value } ?: UNKNOWN
    }
}