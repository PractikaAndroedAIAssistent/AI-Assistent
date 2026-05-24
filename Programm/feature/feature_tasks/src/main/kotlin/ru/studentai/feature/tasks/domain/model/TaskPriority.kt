package ru.studentai.feature.tasks.domain.model

/**
 * Приоритет задачи (ТЗ §4.2.4: сортировка по приоритету).
 *
 * Ordinal монотонно растёт с важностью — допустимо `sortedByDescending { it.priority.ordinal }`.
 */
public enum class TaskPriority {
    Low,
    Normal,
    High,
    Critical,
}
