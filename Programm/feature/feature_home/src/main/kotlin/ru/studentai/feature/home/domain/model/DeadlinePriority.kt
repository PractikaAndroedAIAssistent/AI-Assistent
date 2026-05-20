package ru.studentai.feature.home.domain.model

/**
 * Приоритет дедлайна. Влияет на сортировку (см. ТЗ §4.2.4 — сортировка по приоритету)
 * и цветовую индикацию в [WeekDeadlinesCard].
 *
 * `ordinal` повышается с важностью — можно сортировать `compareByDescending { it.priority.ordinal }`.
 */
public enum class DeadlinePriority {
    Low,
    Normal,
    High,
    Critical,
}
