package ru.studentai.feature.tasks.domain.model

/** Способ сортировки списка задач (ТЗ §4.2.4). */
public enum class TaskSort {
    ByDate,
    BySubject,
    ByPriority,
}

/**
 * Фильтр списка.
 *
 * @param subjectId       null = все предметы; иначе только указанный
 * @param showCompleted   показывать выполненные
 * @param onlyOverdue     показывать только просроченные
 */
public data class TaskFilter(
    public val subjectId: String? = null,
    public val showCompleted: Boolean = true,
    public val onlyOverdue: Boolean = false,
) {
    public companion object {
        public val ALL: TaskFilter = TaskFilter()
        public val ACTIVE: TaskFilter = TaskFilter(showCompleted = false)
        public val OVERDUE_ONLY: TaskFilter = TaskFilter(onlyOverdue = true, showCompleted = false)
        public val COMPLETED_ONLY: TaskFilter = TaskFilter(showCompleted = true)
            // for COMPLETED_ONLY UI ставит дополнительный фильтр isCompleted == true сам в VM
    }
}
