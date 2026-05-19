package ru.studentai.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * `List<String> ↔ JSON string`.
 *
 * Используется для хранения списков тэгов, опций ответа, и т.п. в одной строке.
 * Для production-сценариев с большими/изменяемыми списками лучше нормализовать
 * в отдельную таблицу — этот converter подходит для **малых** статических списков.
 *
 * JSON-сериализация выбрана вместо CSV, чтобы корректно обрабатывать запятые
 * и спецсимволы внутри элементов.
 */
public class StringListConverter {

    @TypeConverter
    public fun fromList(value: List<String>?): String? =
        value?.let { Json.encodeToString(LIST_SERIALIZER, it) }

    @TypeConverter
    public fun toList(value: String?): List<String>? =
        value?.let { Json.decodeFromString(LIST_SERIALIZER, it) }

    private companion object {
        val LIST_SERIALIZER = ListSerializer(String.serializer())
    }
}
