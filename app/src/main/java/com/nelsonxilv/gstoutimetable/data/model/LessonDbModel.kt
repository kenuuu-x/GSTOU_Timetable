package com.nelsonxilv.gstoutimetable.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.DayOfWeek
import com.nelsonxilv.gstoutimetable.domain.entity.TimeInterval

@Entity(tableName = "lessons")
data class LessonDbModel(
    @PrimaryKey val lessonId: Int,
    val name: String,
    val teacher: String,
    val auditorium: String,
    val groupNames: List<String>,
    @Embedded val timeInterval: TimeInterval,
    val activityType: ActivityType,
    val period: Int,
    val dayOfWeek: DayOfWeek,
    val week: Int,
    val subgroupNumber: Int,
)
