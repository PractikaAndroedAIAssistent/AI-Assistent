package ru.studentai.tests.tasks

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.integration.home.TasksNearestDeadlinesProvider
import ru.studentai.tests.tasks.support.FakeTaskRepository
import ru.studentai.tests.tasks.support.TaskFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class TasksNearestDeadlinesProviderTest {

    @Test
    fun `fetch maps student deadlines and forwards student role with limit`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Success(
                listOf(
                    TaskFixtures.studentTask(
                        id = "deadline-1",
                        subjectName = "Algorithms",
                        title = "Lab 1",
                        dueAt = TaskFixtures.dateTime("2000-01-01T10:00:00"),
                        priority = TaskPriority.Critical,
                    ),
                ),
            )
        }

        val result = TasksNearestDeadlinesProvider(repository).fetch("student-1", 4)

        assertThat(result).isEqualTo(
            listOf(
                TaskFixtures.deadlineItem(
                    id = "deadline-1",
                    subject = "Algorithms",
                    title = "Lab 1",
                    dueAt = TaskFixtures.dateTime("2000-01-01T10:00:00"),
                    priority = ru.studentai.feature.home.domain.model.DeadlinePriority.Critical,
                    isOverdue = true,
                ),
            ),
        )
        assertThat(repository.lastUpcomingOwnerUserId).isEqualTo("student-1")
        assertThat(repository.lastUpcomingRole).isEqualTo(TaskRole.Student)
        assertThat(repository.lastUpcomingLimit).isEqualTo(4)
    }

    @Test
    fun `fetch ignores teacher assignments in mixed result`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Success(
                listOf(
                    TaskFixtures.teacherTask(id = "teacher-only"),
                ),
            )
        }

        val result = TasksNearestDeadlinesProvider(repository).fetch("student-1", 3)

        assertThat(result).isEmpty()
    }

    @Test
    fun `fetch returns empty list when repository fails`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "x"))
        }

        val result = TasksNearestDeadlinesProvider(repository).fetch("student-1", 2)

        assertThat(result).isEmpty()
    }
}
