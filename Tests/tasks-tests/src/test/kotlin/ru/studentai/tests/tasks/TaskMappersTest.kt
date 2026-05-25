package ru.studentai.tests.tasks

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import ru.studentai.feature.tasks.data.local.entity.TaskEntity
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.tests.tasks.support.TaskFixtures

class TaskMappersTest {

    @Test
    fun `student deadline round-trips through entity`() {
        val task = TaskFixtures.studentTask(
            id = "student-42",
            subjectName = "Databases",
            description = "Prepare ER diagram",
            priority = TaskPriority.High,
        )

        val mapped = toDomain(toEntity(task))

        assertThat(mapped).isEqualTo(task)
    }

    @Test
    fun `teacher assignment round-trips through entity`() {
        val task = TaskFixtures.teacherTask(
            id = "teacher-77",
            subjectName = "Physics",
            description = "Check lab reports",
            groupName = "IU7-43B",
        )

        val mapped = toDomain(toEntity(task))

        assertThat(mapped).isEqualTo(task)
    }

    @Test
    fun `unknown priority parses as Normal fallback`() {
        val entity = TaskFixtures.studentEntity(priority = "UnexpectedPriority")

        val mapped = toDomain(entity)

        assertThat(mapped.priority).isEqualTo(TaskPriority.Normal)
    }

    private fun toEntity(task: StudyTask): TaskEntity {
        val method = Class.forName("ru.studentai.feature.tasks.data.mapper.TaskMappersKt")
            .getDeclaredMethod("toEntity", StudyTask::class.java)
        return method.invoke(null, task) as TaskEntity
    }

    private fun toDomain(entity: TaskEntity): StudyTask {
        val method = Class.forName("ru.studentai.feature.tasks.data.mapper.TaskMappersKt")
            .getDeclaredMethod("toDomain", TaskEntity::class.java)
        return method.invoke(null, entity) as StudyTask
    }
}
