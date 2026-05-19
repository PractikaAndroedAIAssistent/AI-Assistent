package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * `kotlinx-datetime.Instant ↔ Long` (epoch milliseconds).
 *
 * Регистрируется в `@TypeConverters(InstantConverter::class)` на уровне Database.
 */
public class InstantConverter {

    @TypeConverter
    public fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    public fun toInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}
