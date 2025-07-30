package com.nelsonxilv.gstoutimetable.data.mapper.mappers

import com.nelsonxilv.gstoutimetable.domain.entity.TimeInterval
import javax.inject.Inject

class PeriodToTimeMapper @Inject constructor() {

    private val periodToTime = mapOf(
        1 to TimeInterval("9:00", "10:20"),
        2 to TimeInterval("10:30", "11:50"),
        3 to TimeInterval("13:00", "14:20"),
        4 to TimeInterval("14:30", "15:50"),
        5 to TimeInterval("16:00", "17:20"),
        6 to TimeInterval("17:30", "18:50"),
        7 to TimeInterval("19:00", "20:20"),
        8 to TimeInterval("20:30", "21:50")
    )

    private val periodToTimeForFSPO = mapOf(
        1 to TimeInterval("9:00", "10:00"),
        2 to TimeInterval("10:10", "11:10"),
        3 to TimeInterval("11:20", "12:20"),
        4 to TimeInterval("13:00", "14:00"),
        5 to TimeInterval("14:10", "15:10"),
        6 to TimeInterval("15:20", "16:20")
    )

    private val fridayPeriodToTime = mapOf(
        1 to TimeInterval("9:00", "10:20"),
        2 to TimeInterval("10:30", "11:50"),
        3 to TimeInterval("14:00", "15:20"),
        4 to TimeInterval("15:30", "16:50")
    )

    fun getPeriodTime(period: Int, scheduleType: ScheduleType): TimeInterval {
        return when(scheduleType) {
            ScheduleType.FSPO -> periodToTimeForFSPO[period]
            ScheduleType.FRIDAY_STANDARD -> fridayPeriodToTime[period]
            ScheduleType.STANDARD -> periodToTime[period]
        } ?: TimeInterval("00:00", "00:00")
    }
}