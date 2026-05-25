package ru.studentai.feature.tasks.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority

class TaskMappersTest {

    @Test
    fun `student deadline round-trips through entity`() {
        val task = StudyTask.StudentDeadline(
            id = "id1",
            ownerUserId = "u1",
            subjectId = null,
            subjectName = "Algorithms",
            title = "Lab 2",
            description = null,
            dueAt = LocalDateTime.parse("2026-05-22T23:59:00"),
            priority = TaskPriority.High,
        )
        val back = task.toEntity().toDomain()
        assertThat(back).isInstanceOf(StudyTask.StudentDeadline::class)
        assertThat(back).isEqualTo(task)
    }

    @Test
    fun `teacher assignment round-trips through entity`() {
        val task = StudyTask.TeacherAssignment(
            id = "id1",
            ownerUserId = "t1",
            subjectId = null,
            subjectName = "Math",
            title = "Check labs",
            dueAt = LocalDateTime.parse("2026-05-22T15:00:00"),
            priority = TaskPriority.Normal,
            groupName = "ИУ-101",
        )
        val back = task.toEntity().toDomain()
        assertThat(back).isInstanceOf(StudyTask.TeacherAssignment::class)
        assertThat(back).isEqualTo(task)
    }

    @Test
    fun `unknown priority parses as Normal fallback`() {
        val task = StudyTask.StudentDeadline(
            id = "id1",
            ownerUserId = "u1",
            subjectId = null,
            subjectName = null,
            title = "X",
            dueAt = LocalDateTime.parse("2026-05-22T10:00:00"),
            priority = TaskPriority.Low,
        )
        val raw = task.toEntity().copy(priority = "GIBBERISH")
        val back = raw.toDomain()
        assertThat(back.priority).isEqualTo(TaskPriority.Normal)
    }
}
