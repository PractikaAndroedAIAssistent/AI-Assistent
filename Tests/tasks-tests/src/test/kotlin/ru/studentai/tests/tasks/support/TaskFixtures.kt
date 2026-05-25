package ru.studentai.tests.tasks.support

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.DeadlinePriority
import ru.studentai.feature.home.domain.model.TeacherTask
import ru.studentai.feature.tasks.data.local.entity.TaskEntity
import ru.studentai.feature.tasks.data.remote.dto.TaskDto
import ru.studentai.feature.tasks.data.remote.dto.TaskListResponse
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority

internal object TaskFixtures {

    fun date(value: String): LocalDate = LocalDate.parse(value)

    fun time(value: String): LocalTime = LocalTime.parse(value)

    fun dateTime(value: String): LocalDateTime = LocalDateTime.parse(value)

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

    fun teacherUser(
        id: String = "teacher-1",
        email: String = "teacher@example.com",
        fullName: String = "Petr Ivanov",
    ): User = User(
        id = id,
        email = email,
        fullName = fullName,
        role = UserRole.Teacher,
    )

    fun studentProfile(user: User = studentUser()): UserProfile = UserProfile(
        user = user,
        university = "MSU",
        group = "IU7-41B",
        course = 3,
        speciality = "Computer Science",
    )

    fun teacherProfile(user: User = teacherUser()): UserProfile = UserProfile(
        user = user,
        university = "MSU",
        speciality = "Mathematics",
    )

    fun studentTask(
        id: String = "task-1",
        ownerUserId: String = "student-1",
        subjectId: String? = "subject-1",
        subjectName: String? = "Algorithms",
        title: String = "Lab 1",
        description: String? = "Solve tasks 1-4",
        dueAt: LocalDateTime = dateTime("2026-05-26T10:00:00"),
        priority: TaskPriority = TaskPriority.Normal,
        isCompleted: Boolean = false,
        completedAt: LocalDateTime? = null,
    ): StudyTask.StudentDeadline = StudyTask.StudentDeadline(
        id = id,
        ownerUserId = ownerUserId,
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
    )

    fun teacherTask(
        id: String = "task-2",
        ownerUserId: String = "teacher-1",
        subjectId: String? = "subject-2",
        subjectName: String? = "Physics",
        title: String = "Check reports",
        description: String? = "Review submissions",
        dueAt: LocalDateTime = dateTime("2026-05-27T12:00:00"),
        priority: TaskPriority = TaskPriority.High,
        isCompleted: Boolean = false,
        completedAt: LocalDateTime? = null,
        groupName: String = "IU7-42B",
    ): StudyTask.TeacherAssignment = StudyTask.TeacherAssignment(
        id = id,
        ownerUserId = ownerUserId,
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        groupName = groupName,
    )

    fun studentEntity(
        id: String = "task-1",
        ownerUserId: String = "student-1",
        subjectId: String? = "subject-1",
        subjectName: String? = "Algorithms",
        title: String = "Lab 1",
        description: String? = "Solve tasks 1-4",
        dueAt: LocalDateTime = dateTime("2026-05-26T10:00:00"),
        priority: String = TaskPriority.Normal.name,
        isCompleted: Boolean = false,
        completedAt: LocalDateTime? = null,
    ): TaskEntity = TaskEntity(
        id = id,
        ownerUserId = ownerUserId,
        role = "Student",
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        groupName = null,
    )

    fun teacherEntity(
        id: String = "task-2",
        ownerUserId: String = "teacher-1",
        subjectId: String? = "subject-2",
        subjectName: String? = "Physics",
        title: String = "Check reports",
        description: String? = "Review submissions",
        dueAt: LocalDateTime = dateTime("2026-05-27T12:00:00"),
        priority: String = TaskPriority.High.name,
        isCompleted: Boolean = false,
        completedAt: LocalDateTime? = null,
        groupName: String? = "IU7-42B",
    ): TaskEntity = TaskEntity(
        id = id,
        ownerUserId = ownerUserId,
        role = "Teacher",
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        groupName = groupName,
    )

    fun studentDto(
        id: String = "task-1",
        subjectId: String? = "subject-1",
        subjectName: String? = "Algorithms",
        title: String = "Lab 1",
        description: String? = "Solve tasks 1-4",
        dueAt: String = "2026-05-26T10:00:00",
        priority: String = TaskPriority.Normal.name,
        isCompleted: Boolean = false,
        completedAt: String? = null,
    ): TaskDto = TaskDto(
        id = id,
        role = "Student",
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        groupName = null,
    )

    fun teacherDto(
        id: String = "task-2",
        subjectId: String? = "subject-2",
        subjectName: String? = "Physics",
        title: String = "Check reports",
        description: String? = "Review submissions",
        dueAt: String = "2026-05-27T12:00:00",
        priority: String = TaskPriority.High.name,
        isCompleted: Boolean = false,
        completedAt: String? = null,
        groupName: String? = "IU7-42B",
    ): TaskDto = TaskDto(
        id = id,
        role = "Teacher",
        subjectId = subjectId,
        subjectName = subjectName,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        groupName = groupName,
    )

    fun taskListResponse(items: List<TaskDto>): TaskListResponse = TaskListResponse(items)

    fun deadlineItem(
        id: String = "task-1",
        subject: String = "Algorithms",
        title: String = "Lab 1",
        dueAt: LocalDateTime = dateTime("2026-05-26T10:00:00"),
        priority: DeadlinePriority = DeadlinePriority.Normal,
        isOverdue: Boolean = false,
    ): DeadlineItem = DeadlineItem(
        id = id,
        subject = subject,
        title = title,
        dueAt = dueAt,
        priority = priority,
        isOverdue = isOverdue,
    )

    fun teacherHomeTask(
        id: String = "task-2",
        title: String = "Check reports",
        dueAt: LocalDateTime = dateTime("2026-05-27T12:00:00"),
        relatedSubject: String? = "Physics",
        isOverdue: Boolean = false,
    ): TeacherTask = TeacherTask(
        id = id,
        title = title,
        dueAt = dueAt,
        relatedSubject = relatedSubject,
        isOverdue = isOverdue,
    )
}
