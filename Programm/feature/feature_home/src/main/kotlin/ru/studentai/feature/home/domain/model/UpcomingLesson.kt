package ru.studentai.feature.home.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Ближайшее занятие в расписании.
 *
 * Подмножество полей `ScheduleItem` из feature_schedule, достаточное для карточки
 * на главной (ТЗ §4.2.3: время, предмет, тип занятия, аудитория, преподаватель).
 *
 * @param startAt        дата-время начала
 * @param endAt          дата-время окончания
 * @param subject        название предмета
 * @param lessonType     тип занятия (Лекция / Семинар / Лабораторная — в виде строки,
 *                       enum находится в feature_schedule)
 * @param room           аудитория (может отсутствовать для удалённых занятий)
 * @param teacher        ФИО преподавателя (опционально)
 */
public data class UpcomingLesson(
    public val startAt: LocalDateTime,
    public val endAt: LocalDateTime,
    public val subject: String,
    public val lessonType: String,
    public val room: String? = null,
    public val teacher: String? = null,
)
