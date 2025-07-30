package com.nelsonxilv.gstoutimetable.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nelsonxilv.gstoutimetable.data.model.GroupDbModel
import com.nelsonxilv.gstoutimetable.data.model.LessonDbModel

@Database(
    entities = [
        LessonDbModel::class,
        GroupLessonCrossRef::class,
        GroupDbModel::class
    ],
    version = 3
)
@TypeConverters(ListConverter::class, EnumConverter::class)
abstract class TimetableDatabase : RoomDatabase() {
    abstract fun lessonDao(): TimetableDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE groups RENAME TO student_groups")
    }
}