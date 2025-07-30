package com.nelsonxilv.gstoutimetable.domain.usecase

import com.nelsonxilv.gstoutimetable.domain.DateProvider
import com.nelsonxilv.gstoutimetable.domain.TimetableRepository
import com.nelsonxilv.gstoutimetable.domain.entity.Day
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLessonListForWeekUseCase @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val dateProvider: DateProvider,
) {
    suspend operator fun invoke(groupName: String): Flow<List<Day>> {
        val allWeekDayList = dateProvider.getAllDaysOfWeek()

        return timetableRepository.getLessonList(groupName).map { lessons ->
            val lessonsByDayOfWeek = lessons.groupBy { it.dayOfWeek.rawValue }

            allWeekDayList.map { (dayOfWeekNumber, dayName) ->
                val dayLessons = lessonsByDayOfWeek[dayOfWeekNumber] ?: emptyList()
                val correctedDayName = dayName.replaceFirstChar { it.uppercase() }
                Day(correctedDayName, dayLessons.sortedBy { it.period })
            }
        }
    }
}