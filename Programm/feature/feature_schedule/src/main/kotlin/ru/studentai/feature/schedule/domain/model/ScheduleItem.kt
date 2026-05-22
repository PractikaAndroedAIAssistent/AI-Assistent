package ru.studentai.feature.schedule.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Одно занятие в расписании (ТЗ §4.2.3).
 *
 * @param id              стабильный идентификатор (UUID/серверный)
 * @param ownerUserId     владелец расписания (студент = свой график; преподаватель = ведёт)
 * @param subjectId       FK на [Subject]
 * @param subjectName     денормализованное имя предмета (избегаем join'а в hot-path списках)
 * @param lessonType      [LessonType]
 * @param customTypeLabel произвольная подпись типа (только для [LessonType.Other])
 * @param startAt         дата-время начала
 * @param endAt           дата-время окончания
 * @param room            аудитория
 * @param teacher         ФИО преподавателя
 * @param note            свободное примечание пользователя
 */
public data class ScheduleItem(
    public val id: String,
    public val ownerUserId: String,
    public val subjectId: String,
    public val subjectName: String,
    public val lessonType: LessonType,
    public val customTypeLabel: String? = null,
    public val startAt: LocalDateTime,
    public val endAt: LocalDateTime,
    public val room: String? = null,
    public val teacher: String? = null,
    public val note: String? = null,
) {
    init {
        require(endAt > startAt) {
            "endAt ($endAt) must be after startAt ($startAt)"
        }
        require(lessonType != LessonType.Other || !customTypeLabel.isNullOrBlank()) {
            "customTypeLabel must be provided when lessonType == Other"
        }
    }
}
