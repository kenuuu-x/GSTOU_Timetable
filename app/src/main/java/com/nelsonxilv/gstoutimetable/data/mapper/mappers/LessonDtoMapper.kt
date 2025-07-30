package com.nelsonxilv.gstoutimetable.data.mapper.mappers

import com.nelsonxilv.gstoutimetable.data.model.LessonDbModel
import com.nelsonxilv.gstoutimetable.data.network.model.DisciplineDto
import com.nelsonxilv.gstoutimetable.data.network.model.GroupDto
import com.nelsonxilv.gstoutimetable.data.network.model.LessonDto
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.DayOfWeek
import com.nelsonxilv.gstoutimetable.domain.entity.TimeInterval
import javax.inject.Inject

class LessonDtoMapper @Inject constructor(
    private val periodToTimeMapper: PeriodToTimeMapper
) : Mapper<LessonDto, LessonDbModel> {

    override fun transform(data: LessonDto) = LessonDbModel(
        lessonId = data.id ?: NO_DATA_VALUE,
        name = data.disciplineDto?.name ?: EMPTY_DISCIPLINE_VALUE,
        teacher = getTeacherName(
            ActivityType.fromInt(data.activityType ?: NO_DATA_VALUE),
            data.disciplineDto
        ),
        auditorium = getAuditoriumName(data.auditoriumDto?.name),
        groupNames = mapGroupsDtoToString(data.groupsDto),
        timeInterval = calculatePeriodTimeInterval(data),
        activityType = ActivityType.fromInt(data.activityType ?: NO_DATA_VALUE),
        period = data.period,
        dayOfWeek = DayOfWeek.fromInt(data.weekDay ?: NO_DATA_VALUE),
        week = data.week ?: NO_DATA_VALUE,
        subgroupNumber = data.subgroupNumber ?: NO_DATA_VALUE
    )

    private fun getTeacherName(activityType: ActivityType, disciplineDto: DisciplineDto?): String {
        val teacherName = when (activityType) {
            ActivityType.LECTURE -> disciplineDto?.lectureTeacherDto?.name
            ActivityType.LAB -> disciplineDto?.labTeacherDto?.name
            ActivityType.PRACTICE -> disciplineDto?.practiceTeacherDto?.name
            else -> EMPTY_TEACHER_VALUE
        } ?: EMPTY_TEACHER_VALUE

        return formatName(teacherName)
    }

    private fun getAuditoriumName(name: String?) =
        if (name.isNullOrBlank()) EMPTY_ROOM_VALUE else name

    private fun mapGroupsDtoToString(dtoList: List<GroupDto>?): List<String> =
        dtoList?.mapNotNull { it.name } ?: emptyList()

    private fun calculatePeriodTimeInterval(data: LessonDto): TimeInterval {
        val scheduleType = determineScheduleType(
            data.groupsDto,
            DayOfWeek.fromInt(data.weekDay)
        )

        return periodToTimeMapper.getPeriodTime(data.period, scheduleType)
    }

    private fun determineScheduleType(
        listGroups: List<GroupDto>?,
        dayOfWeek: DayOfWeek
    ): ScheduleType {
        val isFSPO = listGroups?.all { it.instituteDto?.name == FSPO } == true
        if (isFSPO) {
            return ScheduleType.FSPO
        }

        return if (dayOfWeek == DayOfWeek.FRIDAY) {
            ScheduleType.FRIDAY_STANDARD
        } else {
            ScheduleType.STANDARD
        }
    }

    private fun formatName(fullName: String): String {
        val parts = fullName.trim().split(SPACE).filter { it.isNotEmpty() }
        return if (parts.size > 1) {
            parts[0] + SPACE + parts.drop(1).joinToString(SPACE) { "${it[0]}." }
        } else {
            fullName
        }
    }

    companion object {
        private const val FSPO = "ФСПО"
        private const val NO_DATA_VALUE = 0
        private const val SPACE = " "
        private const val EMPTY_TEACHER_VALUE = "No teacher"
        private const val EMPTY_ROOM_VALUE = "No room"
        private const val EMPTY_DISCIPLINE_VALUE = "No discipline"
    }

}