package ru.studentai.tests.schedule.support

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.schedule.data.local.entity.ScheduleItemEntity
import ru.studentai.feature.schedule.data.local.entity.SubjectEntity
import ru.studentai.feature.schedule.data.remote.dto.ImportResponse
import ru.studentai.feature.schedule.data.remote.dto.ScheduleItemDto
import ru.studentai.feature.schedule.data.remote.dto.ScheduleSyncResponse
import ru.studentai.feature.schedule.data.remote.dto.SubjectDto
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject

internal object ScheduleFixtures {

    fun studentUser(
        id: String = "student-1",
        email: String = "student@example.com",
        fullName: String = "Ivan Petrov",
    ): User = User(
        id = id,
        email = email,
        fullName = fullName,
        role = UserRole.Student,
    )

    fun studentProfile(user: User = studentUser()): UserProfile = UserProfile(
        user = user,
        university = "MSU",
        group = "IU7-41B",
        course = 3,
        speciality = "Computer Science",
    )

    fun subject(
        id: String = "subject-1",
        name: String = "Algorithms",
    ): Subject = Subject(
        id = id,
        name = name,
    )

    fun subjectEntity(
        id: String = "subject-1",
        ownerUserId: String = "student-1",
        name: String = "Algorithms",
    ): SubjectEntity = SubjectEntity(
        id = id,
        ownerUserId = ownerUserId,
        name = name,
    )

    fun scheduleItem(
        id: String = "lesson-1",
        ownerUserId: String = "student-1",
        subjectId: String = "subject-1",
        subjectName: String = "Algorithms",
        lessonType: LessonType = LessonType.Lecture,
        customTypeLabel: String? = null,
        startAt: LocalDateTime = LocalDateTime.parse("2026-05-20T10:00:00"),
        endAt: LocalDateTime = LocalDateTime.parse("2026-05-20T11:30:00"),
        room: String? = "305",
        teacher: String? = "P. Petrov",
        note: String? = null,
    ): ScheduleItem = ScheduleItem(
        id = id,
        ownerUserId = ownerUserId,
        subjectId = subjectId,
        subjectName = subjectName,
        lessonType = lessonType,
        customTypeLabel = resolvedCustomTypeLabel(lessonType, customTypeLabel),
        startAt = startAt,
        endAt = endAt,
        room = room,
        teacher = teacher,
        note = note,
    )

    fun lesson(
        id: String = "lesson-1",
        ownerUserId: String = "student-1",
        subjectId: String = "subject-1",
        subjectName: String = "Algorithms",
        lessonType: LessonType = LessonType.Lecture,
        customTypeLabel: String? = null,
        startAt: LocalDateTime = LocalDateTime.parse("2026-05-20T10:00:00"),
        endAt: LocalDateTime = LocalDateTime.parse("2026-05-20T11:30:00"),
        room: String? = "305",
        teacher: String? = "P. Petrov",
        note: String? = null,
    ): ScheduleItem = scheduleItem(
        id = id,
        ownerUserId = ownerUserId,
        subjectId = subjectId,
        subjectName = subjectName,
        lessonType = lessonType,
        customTypeLabel = customTypeLabel,
        startAt = startAt,
        endAt = endAt,
        room = room,
        teacher = teacher,
        note = note,
    )

    fun scheduleItemEntity(
        id: String = "lesson-1",
        ownerUserId: String = "student-1",
        subjectId: String? = "subject-1",
        subjectName: String = "Algorithms",
        lessonType: String = "Lecture",
        customTypeLabel: String? = null,
        startAt: LocalDateTime = LocalDateTime.parse("2026-05-20T10:00:00"),
        endAt: LocalDateTime = LocalDateTime.parse("2026-05-20T11:30:00"),
        room: String? = "305",
        teacher: String? = "P. Petrov",
        note: String? = null,
    ): ScheduleItemEntity = ScheduleItemEntity(
        id = id,
        ownerUserId = ownerUserId,
        subjectId = subjectId,
        subjectName = subjectName,
        lessonType = lessonType,
        customTypeLabel = resolvedCustomTypeLabel(lessonType, customTypeLabel),
        startAt = startAt,
        endAt = endAt,
        room = room,
        teacher = teacher,
        note = note,
    )

    fun scheduleItemDto(
        id: String = "lesson-1",
        subjectId: String? = "subject-1",
        subjectName: String = "Algorithms",
        lessonType: String = "Lecture",
        customTypeLabel: String? = null,
        startAt: String = "2026-05-20T10:00:00",
        endAt: String = "2026-05-20T11:30:00",
        room: String? = "305",
        teacher: String? = "P. Petrov",
        note: String? = null,
    ): ScheduleItemDto = ScheduleItemDto(
        id = id,
        subjectId = subjectId,
        subjectName = subjectName,
        lessonType = lessonType,
        customTypeLabel = resolvedCustomTypeLabel(lessonType, customTypeLabel),
        startAt = startAt,
        endAt = endAt,
        room = room,
        teacher = teacher,
        note = note,
    )

    fun scheduleItemFromDto(
        dto: ScheduleItemDto,
        ownerUserId: String = "student-1",
    ): ScheduleItem = scheduleItem(
        id = dto.id,
        ownerUserId = ownerUserId,
        subjectId = dto.subjectId.orEmpty(),
        subjectName = dto.subjectName,
        lessonType = LessonType.valueOf(dto.lessonType),
        customTypeLabel = dto.customTypeLabel,
        startAt = LocalDateTime.parse(dto.startAt),
        endAt = LocalDateTime.parse(dto.endAt),
        room = dto.room,
        teacher = dto.teacher,
        note = dto.note,
    )

    fun subjectDto(
        id: String = "subject-1",
        name: String = "Algorithms",
    ): SubjectDto = SubjectDto(
        id = id,
        name = name,
    )

    fun syncResponse(
        subjects: List<SubjectDto> = listOf(subjectDto()),
        items: List<ScheduleItemDto> = listOf(scheduleItemDto()),
    ): ScheduleSyncResponse = ScheduleSyncResponse(
        subjects = subjects,
        items = items,
    )

    fun importResponse(importedCount: Int = 2): ImportResponse = ImportResponse(
        importedCount = importedCount,
    )

    fun date(value: String): LocalDate = LocalDate.parse(value)

    fun time(value: String): LocalTime = LocalTime.parse(value)

    fun upcomingInstant(): Instant = Instant.parse("2026-05-20T08:00:00Z")

    private fun resolvedCustomTypeLabel(
        lessonType: LessonType,
        customTypeLabel: String?,
    ): String? = if (lessonType == LessonType.Other) {
        customTypeLabel ?: "Workshop"
    } else {
        customTypeLabel
    }

    private fun resolvedCustomTypeLabel(
        lessonType: String,
        customTypeLabel: String?,
    ): String? = if (lessonType == LessonType.Other.name) {
        customTypeLabel ?: "Workshop"
    } else {
        customTypeLabel
    }
}
