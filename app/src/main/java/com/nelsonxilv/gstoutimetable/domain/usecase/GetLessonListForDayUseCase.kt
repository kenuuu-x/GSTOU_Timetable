package com.nelsonxilv.gstoutimetable.domain.usecase

import com.nelsonxilv.gstoutimetable.domain.DateProvider
import com.nelsonxilv.gstoutimetable.domain.DateType
import com.nelsonxilv.gstoutimetable.domain.TimetableRepository
import com.nelsonxilv.gstoutimetable.domain.entity.Lesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLessonListForDayUseCase @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val dateProvider: DateProvider
) {
    suspend operator fun invoke(
        groupName: String,
        dateType: DateType
    ): Flow<List<Lesson>> {
        val dayOfWeek = dateProvider.getDayOfWeekNumber(dateType)
        val weekNumber = dateProvider.getWeekNumber(dateType)

        return timetableRepository.getLessonList(groupName).map { lessons ->
            lessons.filterLessons(dayOfWeek, weekNumber)
        }
    }

    private fun List<Lesson>.filterLessons(
        dayOfWeek: Int? = null,
        weekNumber: Int? = null
    ): List<Lesson> {
        return this.filter { lesson ->
            (dayOfWeek == null || lesson.dayOfWeek.rawValue == dayOfWeek) &&
                    (weekNumber == null || lesson.week == weekNumber
                            || lesson.week == NO_WEEK)
        }.sortedBy { lesson ->
            lesson.period
        }
    }

    companion object {
        private const val NO_WEEK = 0
    }
}