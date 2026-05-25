package ru.studentai.tests.tasks

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.usecase.DeleteTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.GetTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.GetUpcomingTasksUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTaskSubjectsUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTasksUseCase
import ru.studentai.feature.tasks.domain.usecase.RefreshTasksUseCase
import ru.studentai.feature.tasks.domain.usecase.ToggleTaskCompletionUseCase
import ru.studentai.feature.tasks.domain.usecase.UpsertTaskUseCase
import ru.studentai.tests.tasks.support.FakeTaskRepository
import ru.studentai.tests.tasks.support.TaskFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class TaskUseCasesTest {

    @Test
    fun `observe tasks use case delegates owner role and filter to repository`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasksFlow.value = listOf(TaskFixtures.studentTask(title = "Observe me"))
        }
        val filter = TaskFilter(subjectId = "subject-1", showCompleted = false, onlyOverdue = true)

        ObserveTasksUseCase(repository)("student-1", TaskRole.Student, filter).test {
            assertThat(awaitItem()).isEqualTo(listOf(TaskFixtures.studentTask(title = "Observe me")))
        }

        assertThat(repository.observeTasksCallCount).isEqualTo(1)
        assertThat(repository.lastObserveOwnerUserId).isEqualTo("student-1")
        assertThat(repository.lastObserveRole).isEqualTo(TaskRole.Student)
        assertThat(repository.lastObserveFilter).isEqualTo(filter)
    }

    @Test
    fun `observe task subjects use case exposes repository flow`() = runTest {
        val repository = FakeTaskRepository().apply {
            subjectsFlow.value = listOf("Algorithms", "Physics")
        }

        ObserveTaskSubjectsUseCase(repository)("teacher-1", TaskRole.Teacher).test {
            assertThat(awaitItem()).isEqualTo(listOf("Algorithms", "Physics"))
        }

        assertThat(repository.observeSubjectsCallCount).isEqualTo(1)
        assertThat(repository.lastSubjectsOwnerUserId).isEqualTo("teacher-1")
        assertThat(repository.lastSubjectsRole).isEqualTo(TaskRole.Teacher)
    }

    @Test
    fun `get task use case returns repository result`() = runTest {
        val repository = FakeTaskRepository().apply {
            getByIdResult = DomainResult.Success(TaskFixtures.teacherTask(id = "task-4"))
        }

        val result = GetTaskUseCase(repository)("task-4")

        assertThat(result).isEqualTo(repository.getByIdResult)
        assertThat(repository.getByIdCallCount).isEqualTo(1)
        assertThat(repository.lastGetByIdId).isEqualTo("task-4")
    }

    @Test
    fun `get upcoming tasks use case delegates role and limit`() = runTest {
        val repository = FakeTaskRepository().apply {
            getUpcomingResult = DomainResult.Success(listOf(TaskFixtures.teacherTask(id = "task-9")))
        }

        val result = GetUpcomingTasksUseCase(repository)("teacher-1", TaskRole.Teacher, 6)

        assertThat(result).isEqualTo(repository.getUpcomingResult)
        assertThat(repository.getUpcomingCallCount).isEqualTo(1)
        assertThat(repository.lastUpcomingOwnerUserId).isEqualTo("teacher-1")
        assertThat(repository.lastUpcomingRole).isEqualTo(TaskRole.Teacher)
        assertThat(repository.lastUpcomingLimit).isEqualTo(6)
    }

    @Test
    fun `upsert task use case delegates task to repository`() = runTest {
        val repository = FakeTaskRepository().apply {
            upsertResult = DomainResult.Success(TaskFixtures.studentTask(id = "saved-1"))
        }
        val task = TaskFixtures.studentTask(id = "saved-1")

        val result = UpsertTaskUseCase(repository)(task)

        assertThat(result).isEqualTo(repository.upsertResult)
        assertThat(repository.upsertCallCount).isEqualTo(1)
        assertThat(repository.lastUpsertTask).isEqualTo(task)
    }

    @Test
    fun `toggle completion use case delegates id to repository`() = runTest {
        val repository = FakeTaskRepository().apply {
            toggleResult = DomainResult.Success(TaskFixtures.studentTask(id = "task-10", isCompleted = true))
        }

        val result = ToggleTaskCompletionUseCase(repository)("task-10")

        assertThat(result).isEqualTo(repository.toggleResult)
        assertThat(repository.toggleCallCount).isEqualTo(1)
        assertThat(repository.lastToggleId).isEqualTo("task-10")
    }

    @Test
    fun `delete task use case delegates id to repository`() = runTest {
        val repository = FakeTaskRepository().apply {
            deleteResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "task-404"))
        }

        val result = DeleteTaskUseCase(repository)("task-404")

        assertThat(result).isEqualTo(repository.deleteResult)
        assertThat(repository.deleteCallCount).isEqualTo(1)
        assertThat(repository.lastDeleteId).isEqualTo("task-404")
    }

    @Test
    fun `refresh tasks use case delegates owner and role to repository`() = runTest {
        val repository = FakeTaskRepository()

        val result = RefreshTasksUseCase(repository)("teacher-1", TaskRole.Teacher)

        assertThat(result).isEqualTo(DomainResult.Success(Unit))
        assertThat(repository.refreshCallCount).isEqualTo(1)
        assertThat(repository.lastRefreshOwnerUserId).isEqualTo("teacher-1")
        assertThat(repository.lastRefreshRole).isEqualTo(TaskRole.Teacher)
    }
}
