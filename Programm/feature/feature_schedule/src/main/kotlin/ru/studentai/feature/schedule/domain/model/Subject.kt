package ru.studentai.feature.schedule.domain.model

/**
 * Учебный предмет. Сейчас представлен минимальной парой `(id, name)` —
 * расширенная модель (цвет/иконка/преподаватель по умолчанию) появится, когда
 * понадобится отдельный экран управления предметами.
 *
 * `id` хранится как стабильная строка (UUID или серверный идентификатор).
 */
public data class Subject(
    public val id: String,
    public val name: String,
)
