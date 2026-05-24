package ru.studentai.feature.tasks.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Учебная задача — обобщение дедлайна студента и задачи преподавателя (ТЗ §4.2.4).
 *
 * Sealed-иерархия даёт exhaustive `when` в UI и одинаковый код в репозитории,
 * различая роль через type discriminator. Общие поля вынесены в интерфейс.
 *
 * Преподавательский вариант имеет дополнительное поле [TeacherAssignment.groupName] —
 * целевая группа, для которой создано задание.
 */
public sealed interface StudyTask {

    public val id: String
    public val ownerUserId: String
    public val subjectId: String?
    public val subjectName: String?
    public val title: String
    public val description: String?
    public val dueAt: LocalDateTime
    public val priority: TaskPriority
    public val isCompleted: Boolean
    public val completedAt: LocalDateTime?

    /** Дедлайн студента. */
    public data class StudentDeadline(
        override val id: String,
        override val ownerUserId: String,
        override val subjectId: String?,
        override val subjectName: String?,
        override val title: String,
        override val description: String? = null,
        override val dueAt: LocalDateTime,
        override val priority: TaskPriority,
        override val isCompleted: Boolean = false,
        override val completedAt: LocalDateTime? = null,
    ) : StudyTask

    /** Задача преподавателя (проверка работ, подготовка материалов и т. п.). */
    public data class TeacherAssignment(
        override val id: String,
        override val ownerUserId: String,
        override val subjectId: String?,
        override val subjectName: String?,
        override val title: String,
        override val description: String? = null,
        override val dueAt: LocalDateTime,
        override val priority: TaskPriority,
        override val isCompleted: Boolean = false,
        override val completedAt: LocalDateTime? = null,
        public val groupName: String,
    ) : StudyTask {
        init {
            require(groupName.isNotBlank()) { "TeacherAssignment.groupName must not be blank" }
        }
    }
}

/** Утилита: вычислить, просрочена ли задача относительно момента времени. */
public fun StudyTask.isOverdueAt(now: LocalDateTime): Boolean =
    !isCompleted && dueAt < now
