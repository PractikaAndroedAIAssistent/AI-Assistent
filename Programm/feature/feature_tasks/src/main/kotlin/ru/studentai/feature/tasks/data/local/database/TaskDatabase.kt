package ru.studentai.feature.tasks.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.studentai.core.database.converter.InstantConverter
import ru.studentai.core.database.converter.LocalDateConverter
import ru.studentai.core.database.converter.LocalDateTimeConverter
import ru.studentai.feature.tasks.data.local.dao.TaskDao
import ru.studentai.feature.tasks.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = TaskDatabase.VERSION,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
)
public abstract class TaskDatabase : RoomDatabase() {

    public abstract fun taskDao(): TaskDao

    public companion object {
        public const val NAME: String = "studentai_tasks.db"
        public const val VERSION: Int = 1
    }
}
