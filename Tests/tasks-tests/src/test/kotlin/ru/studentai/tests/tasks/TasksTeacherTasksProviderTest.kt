package ru.studentai.tests.tasks

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.integration.home.TasksTeacherTasksProvider
import ru.studentai.tests.tasks.support.FakeTaskRepository
import ru.studentai.tests.tasks.support.TaskFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class TasksTeacherTasksProviderTest {

    @Test
    fun `fetch maps teacher tasks and forwards teacher role with limit`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Success(
                listOf(
                    TaskFixtures.teacherTask(
                        id = "teacher-1",
                        title = "Check essays",
                        dueAt = TaskFixtures.dateTime("2000-01-02T08:00:00"),
                        subjectName = "Literature",
                    ),
                ),
            )
        }

        val result = TasksTeacherTasksProvider(repository).fetch("teacher-1", 5)

        assertThat(result).isEqualTo(
            listOf(
                TaskFixtures.teacherHomeTask(
                    id = "teacher-1",
                    title = "Check essays",
                    dueAt = TaskFixtures.dateTime("2000-01-02T08:00:00"),
                    relatedSubject = "Literature",
                    isOverdue = true,
                ),
            ),
        )
        assertThat(repository.lastUpcomingOwnerUserId).isEqualTo("teacher-1")
        assertThat(repository.lastUpcomingRole).isEqualTo(TaskRole.Teacher)
        assertThat(repository.lastUpcomingLimit).isEqualTo(5)
    }

    @Test
    fun `fetch ignores student deadlines in mixed result`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Success(listOf(TaskFixtures.studentTask(id = "student-only")))
        }

        val result = TasksTeacherTasksProvider(repository).fetch("teacher-1", 2)

        assertThat(result).isEmpty()
    }

    @Test
    fun `fetch returns empty list when repository fails`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "x"))
        }

        val result = TasksTeacherTasksProvider(repository).fetch("teacher-1", 2)

        assertThat(result).isEmpty()
    }
}
