package ru.studentai.feature.schedule.domain.model

/**
 * Тип учебного занятия (ТЗ §4.2.3).
 *
 * `Other` — escape-hatch для специфичных типов (модули, кейсы), которые не покрыты
 * стандартными значениями: чтобы UI отображал произвольную строку, см.
 * [ScheduleItem.customTypeLabel].
 */
public enum class LessonType {
    Lecture,
    Seminar,
    Lab,
    Practice,
    Consultation,
    Exam,
    Other,
}
