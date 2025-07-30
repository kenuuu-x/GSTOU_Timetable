package com.nelsonxilv.gstoutimetable.domain.entity

data class Lesson(
    val lessonId: Int,
    val name: String,
    val teacher: String,
    val auditorium: String,
    val groupNames: List<String>,
    val timeInterval: TimeInterval,
    val activityType: ActivityType,
    val period: Int,
    val dayOfWeek: DayOfWeek,
    val week: Int,
    val subgroupNumber: Int,
)
