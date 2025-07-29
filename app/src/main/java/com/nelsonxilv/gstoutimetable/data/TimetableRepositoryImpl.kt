package com.nelsonxilv.gstoutimetable.data

import com.nelsonxilv.gstoutimetable.data.database.GroupWithLessons
import com.nelsonxilv.gstoutimetable.data.database.TimetableDao
import com.nelsonxilv.gstoutimetable.data.mapper.MapperService
import com.nelsonxilv.gstoutimetable.data.model.GroupDbModel
import com.nelsonxilv.gstoutimetable.data.model.LessonDbModel
import com.nelsonxilv.gstoutimetable.data.network.TimetableApiService
import com.nelsonxilv.gstoutimetable.di.IODispatcher
import com.nelsonxilv.gstoutimetable.domain.TimetableRepository
import com.nelsonxilv.gstoutimetable.domain.entity.Group
import com.nelsonxilv.gstoutimetable.domain.entity.Lesson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val apiService: TimetableApiService,
    private val timetableDao: TimetableDao,
    private val mapper: MapperService,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : TimetableRepository {

    override suspend fun getLessonList(groupName: String): Flow<List<Lesson>> =
        timetableDao.getGroupWithLessonsFlow(groupName)
            .onStart {
                if (timetableDao.getGroupWithLessons(groupName) == null) {
                    val lessonsDb = getScheduleFromApi(groupName)
                    cacheLessons(groupName, lessonsDb)
                }
            }
            .map { groupWithLessons ->
                groupWithLessons?.lessons.orEmpty()
            }
            .distinctUntilChanged()
            .map { lessonsDb ->
                mapper.mapListLessonDbToListEntity(lessonsDb)
            }
            .flowOn(ioDispatcher)

    override suspend fun deleteGroupAndLessons(groupName: String) {
        withContext(ioDispatcher) {
            timetableDao.deleteGroupWithLessons(groupName)
        }
    }

    override fun getGroupList(): Flow<List<Group>> =
        timetableDao.getAllGroups().map { listGroupDb ->
            mapper.mapListGroupDbToListEntity(listGroupDb).reversed()
        }.flowOn(ioDispatcher)

    override suspend fun updateData() {
        val semaphore = Semaphore(SEMAPHORE_SIZE)
        withContext(ioDispatcher) {
            val listGroupDb = timetableDao.getAllGroups().first()
            if (listGroupDb.isNotEmpty()) {
                val jobs = listGroupDb.map { group ->
                    async {
                        semaphore.acquire()
                        try {
                            val newLessonList = getScheduleFromApi(group.groupName)
                            cacheLessons(group.groupName, newLessonList)
                        } finally {
                            semaphore.release()
                        }
                    }
                }
                jobs.awaitAll()
            }
        }
    }

    private suspend fun getScheduleFromApi(groupName: String): List<LessonDbModel> {
        val lessonDtoList = apiService.getSchedule(groupName)
        val lessonDbList = mapper.mapListLessonDtoToListDbModel(lessonDtoList)

        return lessonDbList
    }

    private suspend fun cacheLessons(groupName: String, lessonDbModels: List<LessonDbModel>) {
        val group = GroupDbModel(groupName)
        val groupWithLessons = GroupWithLessons(group, lessonDbModels)

        timetableDao.insertGroupWithLessons(groupWithLessons)
    }

    companion object {
        private const val SEMAPHORE_SIZE = 3
    }

}