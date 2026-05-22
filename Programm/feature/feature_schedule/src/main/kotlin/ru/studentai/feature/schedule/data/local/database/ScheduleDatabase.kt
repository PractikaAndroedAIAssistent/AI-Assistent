package ru.studentai.feature.schedule.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.studentai.core.database.converter.InstantConverter
import ru.studentai.core.database.converter.LocalDateConverter
import ru.studentai.core.database.converter.LocalDateTimeConverter
import ru.studentai.feature.schedule.data.local.dao.ScheduleDao
import ru.studentai.feature.schedule.data.local.dao.SubjectDao
import ru.studentai.feature.schedule.data.local.entity.ScheduleItemEntity
import ru.studentai.feature.schedule.data.local.entity.SubjectEntity

/**
 * Room-БД фичи расписания.
 *
 * Конвертеры берутся из `core_database` — единый источник истины для всех типов.
 */
@Database(
    entities = [
        ScheduleItemEntity::class,
        SubjectEntity::class,
    ],
    version = ScheduleDatabase.VERSION,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
)
public abstract class ScheduleDatabase : RoomDatabase() {

    public abstract fun scheduleDao(): ScheduleDao
    public abstract fun subjectDao(): SubjectDao

    public companion object {
        public const val NAME: String = "studentai_schedule.db"
        public const val VERSION: Int = 1
    }
}
