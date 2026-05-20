package ru.studentai.feature.home.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Элемент списка дедлайнов на главной. См. ТЗ §4.2.4.
 *
 * @param id          стабильный идентификатор для key в LazyColumn
 * @param subject     название предмета (короткое — для бейджа)
 * @param title       короткое описание задания
 * @param dueAt       дата-время дедлайна
 * @param priority    приоритет (см. [DeadlinePriority])
 * @param isOverdue   `true`, если дедлайн уже прошёл и задача не выполнена
 */
public data class DeadlineItem(
    public val id: String,
    public val subject: String,
    public val title: String,
    public val dueAt: LocalDateTime,
    public val priority: DeadlinePriority,
    public val isOverdue: Boolean,
)
