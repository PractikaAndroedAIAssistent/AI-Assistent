package ru.studentai.feature.tasks.data.mapper

import kotlinx.datetime.LocalDateTime
import ru.studentai.feature.tasks.data.local.entity.TaskEntity
import ru.studentai.feature.tasks.data.remote.dto.TaskDto
import ru.studentai.feature.tasks.data.remote.dto.UpsertTaskRequest
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole

// ───────── Domain ↔ Entity ────────────────────────────────────────────────────

internal fun StudyTask.toEntity(): TaskEntity = TaskEntity(
    id = id,
    ownerUserId = ownerUserId,
    role = role().name,
    subjectId = subjectId,
    subjectName = subjectName,
    title = title,
    description = description,
    dueAt = dueAt,
    priority = priority.name,
    isCompleted = isCompleted,
    completedAt = completedAt,
    groupName = (this as? StudyTask.TeacherAssignment)?.groupName,
)

internal fun TaskEntity.toDomain(): StudyTask {
    val parsedRole = parseRole(role)
    val parsedPriority = parsePriority(priority)
    return when (parsedRole) {
        TaskRole.Student -> StudyTask.StudentDeadline(
            id = id,
            ownerUserId = ownerUserId,
            subjectId = subjectId,
            subjectName = subjectName,
            title = title,
            description = description,
            dueAt = dueAt,
            priority = parsedPriority,
            isCompleted = isCompleted,
            completedAt = completedAt,
        )
        TaskRole.Teacher -> StudyTask.TeacherAssignment(
            id = id,
            ownerUserId = ownerUserId,
            subjectId = subjectId,
            subjectName = subjectName,
            title = title,
            description = description,
            dueAt = dueAt,
            priority = parsedPriority,
            isCompleted = isCompleted,
            completedAt = completedAt,
            groupName = groupName.orEmpty().ifBlank { "—" },
        )
    }
}

// ───────── Domain ↔ DTO ───────────────────────────────────────────────────────

internal fun TaskDto.toDomain(ownerUserId: String): StudyTask {
    val role = parseRole(role)
    val priority = parsePriority(priority)
    val parsedDueAt = LocalDateTime.parse(dueAt)
    val parsedCompletedAt = completedAt?.let { LocalDateTime.parse(it) }
    return when (role) {
        TaskRole.Student -> StudyTask.StudentDeadline(
            id = id,
            ownerUserId = ownerUserId,
            subjectId = subjectId,
            subjectName = subjectName,
            title = title,
            description = description,
            dueAt = parsedDueAt,
            priority = priority,
            isCompleted = isCompleted,
            completedAt = parsedCompletedAt,
        )
        TaskRole.Teacher -> StudyTask.TeacherAssignment(
            id = id,
            ownerUserId = ownerUserId,
            subjectId = subjectId,
            subjectName = subjectName,
            title = title,
            description = description,
            dueAt = parsedDueAt,
            priority = priority,
            isCompleted = isCompleted,
            completedAt = parsedCompletedAt,
            groupName = groupName.orEmpty().ifBlank { "—" },
        )
    }
}

internal fun StudyTask.toUpsertRequest(): UpsertTaskRequest = UpsertTaskRequest(
    role = role().name,
    subjectId = subjectId,
    subjectName = subjectName,
    title = title,
    description = description,
    dueAt = dueAt.toString(),
    priority = priority.name,
    isCompleted = isCompleted,
    groupName = (this as? StudyTask.TeacherAssignment)?.groupName,
)

// ───────── Helpers ────────────────────────────────────────────────────────────

internal fun StudyTask.role(): TaskRole = when (this) {
    is StudyTask.StudentDeadline -> TaskRole.Student
    is StudyTask.TeacherAssignment -> TaskRole.Teacher
}

private fun parseRole(raw: String): TaskRole =
    runCatching { TaskRole.valueOf(raw) }.getOrDefault(TaskRole.Student)

private fun parsePriority(raw: String): TaskPriority =
    runCatching { TaskPriority.valueOf(raw) }.getOrDefault(TaskPriority.Normal)
