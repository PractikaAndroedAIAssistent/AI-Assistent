package ru.studentai.feature.schedule.domain.model

/**
 * Фильтр списка расписания (ТЗ §4.2.3: фильтрация по предмету).
 *
 * Не реализован как sealed с одним вариантом нарочно — `null` в [subjectId] = «без фильтра»;
 * добавление новых критериев (по типу/преподавателю) расширит data class новыми полями.
 */
public data class ScheduleFilter(
    public val subjectId: String? = null,
) {
    public companion object {
        public val NONE: ScheduleFilter = ScheduleFilter()
    }
}
