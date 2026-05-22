package ru.studentai.feature.schedule.data.mapper

import kotlinx.datetime.LocalDateTime
import ru.studentai.feature.schedule.data.local.entity.ScheduleItemEntity
import ru.studentai.feature.schedule.data.local.entity.SubjectEntity
import ru.studentai.feature.schedule.data.remote.dto.ScheduleItemDto
import ru.studentai.feature.schedule.data.remote.dto.SubjectDto
import ru.studentai.feature.schedule.data.remote.dto.UpsertScheduleItemRequest
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject

// ───────── Domain ↔ Entity (Room) ─────────────────────────────────────────────

internal fun ScheduleItem.toEntity(): ScheduleItemEntity = ScheduleItemEntity(
    id = id,
    ownerUserId = ownerUserId,
    subjectId = subjectId,
    subjectName = subjectName,
    lessonType = lessonType.name,
    customTypeLabel = customTypeLabel,
    startAt = startAt,
    endAt = endAt,
    room = room,
    teacher = teacher,
    note = note,
)

internal fun ScheduleItemEntity.toDomain(): ScheduleItem = ScheduleItem(
    id = id,
    ownerUserId = ownerUserId,
    subjectId = subjectId ?: "",
    subjectName = subjectName,
    lessonType = parseLessonType(lessonType),
    customTypeLabel = customTypeLabel,
    startAt = startAt,
    endAt = endAt,
    room = room,
    teacher = teacher,
    note = note,
)

internal fun Subject.toEntity(ownerUserId: String): SubjectEntity = SubjectEntity(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
)

internal fun SubjectEntity.toDomain(): Subject = Subject(id = id, name = name)

// ───────── DTO ↔ Domain ───────────────────────────────────────────────────────

internal fun ScheduleItemDto.toDomain(ownerUserId: String): ScheduleItem = ScheduleItem(
    id = id,
    ownerUserId = ownerUserId,
    subjectId = subjectId ?: "",
    subjectName = subjectName,
    lessonType = parseLessonType(lessonType),
    customTypeLabel = customTypeLabel,
    startAt = LocalDateTime.parse(startAt),
    endAt = LocalDateTime.parse(endAt),
    room = room,
    teacher = teacher,
    note = note,
)

internal fun SubjectDto.toDomain(): Subject = Subject(id = id, name = name)

internal fun ScheduleItem.toUpsertRequest(): UpsertScheduleItemRequest = UpsertScheduleItemRequest(
    subjectId = subjectId.ifBlank { null },
    subjectName = subjectName,
    lessonType = lessonType.name,
    customTypeLabel = customTypeLabel,
    startAt = startAt.toString(),
    endAt = endAt.toString(),
    room = room,
    teacher = teacher,
    note = note,
)

// ───────── Helpers ────────────────────────────────────────────────────────────

private fun parseLessonType(raw: String): LessonType =
    runCatching { LessonType.valueOf(raw) }.getOrDefault(LessonType.Other)
