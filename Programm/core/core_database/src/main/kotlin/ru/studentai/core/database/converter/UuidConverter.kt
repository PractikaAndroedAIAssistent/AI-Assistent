package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import java.util.UUID

/**
 * `java.util.UUID ↔ String`.
 *
 * Используется `java.util.UUID` (а не `kotlin.uuid.Uuid`), потому что:
 *  • стабилен (Kotlin Uuid пока экспериментален);
 *  • поддерживается Retrofit/kotlinx-serialization без extra-конфига.
 *
 * Когда `kotlin.uuid.Uuid` стабилизируется, миграция тривиальна — конвертеры дополнятся.
 */
public class UuidConverter {

    @TypeConverter
    public fun fromUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    public fun toUuid(value: String?): UUID? = value?.let(UUID::fromString)
}
