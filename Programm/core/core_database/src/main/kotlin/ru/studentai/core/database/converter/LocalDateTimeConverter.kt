package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

/**
 * `LocalDateTime ↔ String` (ISO-8601 без таймзоны).
 *
 * Например "2026-05-18T09:00:00" — корректно сортируется лексикографически.
 */
public class LocalDateTimeConverter {

    @TypeConverter
    public fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    public fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)
}
