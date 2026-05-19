package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

/**
 * `LocalDate ↔ String` (ISO-8601, например "2026-05-18").
 *
 * Строковое хранение даёт человекочитаемый формат в SQLite и корректную
 * сортировку через ORDER BY date_column ASC (лексикографическая = хронологическая).
 */
public class LocalDateConverter {

    @TypeConverter
    public fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    public fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)
}
