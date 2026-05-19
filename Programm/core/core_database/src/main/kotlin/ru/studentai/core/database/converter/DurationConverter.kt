package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * `kotlin.time.Duration ↔ Long` (millis).
 *
 * Хранится в миллисекундах для совместимости со старыми клиентами/бэкапами,
 * хотя `Duration.INFINITE` отображается как `Long.MAX_VALUE` (приемлемо).
 */
public class DurationConverter {

    @TypeConverter
    public fun fromDuration(value: Duration?): Long? = value?.inWholeMilliseconds

    @TypeConverter
    public fun toDuration(value: Long?): Duration? = value?.milliseconds
}
