package com.nelsonxilv.gstoutimetable.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nelsonxilv.gstoutimetable.data.model.GroupDbModel
import com.nelsonxilv.gstoutimetable.data.model.LessonDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {

    @Query("SELECT * FROM student_groups")
    fun getAllGroups(): Flow<List<GroupDbModel>>

    @Transaction
    @Query("SELECT * FROM student_groups WHERE groupName = :groupName")
    suspend fun getGroupWithLessons(groupName: String): GroupWithLessons?

    @Transaction
    @Query("SELECT * FROM student_groups WHERE groupName = :groupName")
    fun getGroupWithLessonsFlow(groupName: String): Flow<GroupWithLessons?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupLessonCrossRef(crossRef: GroupLessonCrossRef)

    @Transaction
    suspend fun insertGroupWithLessons(groupWithLessons: GroupWithLessons) {

        insertGroup(groupWithLessons.group)

        groupWithLessons.lessons.forEach { lesson ->
            insertLesson(lesson)

            val crossRef = GroupLessonCrossRef(
                groupWithLessons.group.groupName,
                lesson.lessonId
            )
            insertGroupLessonCrossRef(crossRef)
        }

    }

    @Query("SELECT * FROM lesson_group_cross_ref WHERE lessonId = :lessonId")
    suspend fun getGroupsForLesson(lessonId: Int): List<GroupDbModel>

    @Delete
    suspend fun deleteLessons(lessonIds: List<LessonDbModel>)

    @Query("DELETE FROM student_groups WHERE groupName = :groupName")
    suspend fun deleteGroup(groupName: String)

    @Query("DELETE FROM lesson_group_cross_ref WHERE groupName = :groupName")
    suspend fun deleteGroupLessonCrossRef(groupName: String)

    @Transaction
    suspend fun deleteGroupWithLessons(groupName: String) {
        val groupWithLessons = getGroupWithLessons(groupName)
        val lessonsToDelete = groupWithLessons!!.lessons.filter { lesson ->
            val otherGroups = getGroupsForLesson(lesson.lessonId)
            otherGroups.size == 1 && otherGroups[0].groupName == groupName
        }

        deleteLessons(lessonsToDelete)
        deleteGroupLessonCrossRef(groupName)
        deleteGroup(groupName)
    }

}