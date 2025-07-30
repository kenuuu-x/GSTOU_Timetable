package com.nelsonxilv.gstoutimetable.data.database

import androidx.room.TypeConverter
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.DayOfWeek

class EnumConverter {

    @TypeConverter
    fun toActivityType(value: Int): ActivityType = ActivityType.fromInt(value)

    @TypeConverter
    fun fromActivityType(type: ActivityType): Int = type.rawValue

    @TypeConverter
    fun toDayOfWeek(value: Int): DayOfWeek = DayOfWeek.fromInt(value)

    @TypeConverter
    fun fromDayOfWeek(day: DayOfWeek): Int = day.rawValue

}