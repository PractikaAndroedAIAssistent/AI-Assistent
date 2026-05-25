package ru.studentai.tests.tasks

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import java.net.UnknownHostException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.feature.tasks.data.repository.TaskRepositoryImpl
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.tests.tasks.support.FakeTaskDao
import ru.studentai.tests.tasks.support.FakeTasksApi
import ru.studentai.tests.tasks.support.TaskFixtures
import ru.studentai.tests.tasks.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestDispatcherProvider(dispatcher)

    @Test
    fun `observe tasks maps dao entities and forwards filter arguments`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            observeFlow.value = listOf(TaskFixtures.studentEntity(title = "Lab 2"))
        }
        val repository = createRepository(taskDao = taskDao)
        val filter = TaskFilter(subjectId = "subject-1", showCompleted = false, onlyOverdue = true)

        repository.observeTasks("student-1", TaskRole.Student, filter).test {
            assertThat(awaitItem()).isEqualTo(listOf(TaskFixtures.studentTask(title = "Lab 2")))
        }

        assertThat(taskDao.observeCallCount).isEqualTo(1)
        assertThat(taskDao.lastObserveOwnerUserId).isEqualTo("student-1")
        assertThat(taskDao.lastObserveRole).isEqualTo(TaskRole.Student.name)
        assertThat(taskDao.lastObserveSubjectId).isEqualTo("subject-1")
        assertThat(taskDao.lastObserveShowCompleted).isEqualTo(false)
        assertThat(taskDao.lastObserveOnlyOverdue).isEqualTo(true)
        assertThat(taskDao.lastObserveNow).isNotNull()
    }

    @Test
    fun `observe subjects forwards owner and role to dao`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            subjectsFlow.value = listOf("Algorithms", "Physics")
        }
        val repository = createRepository(taskDao = taskDao)

        repository.observeSubjects("teacher-1", TaskRole.Teacher).test {
            assertThat(awaitItem()).isEqualTo(listOf("Algorithms", "Physics"))
        }

        assertThat(taskDao.observeSubjectsCallCount).isEqualTo(1)
        assertThat(taskDao.lastSubjectsOwnerUserId).isEqualTo("teacher-1")
        assertThat(taskDao.lastSubjectsRole).isEqualTo(TaskRole.Teacher.name)
    }

    @Test
    fun `get by id returns not found when dao has no item`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            getByIdResult = null
        }
        val repository = createRepository(taskDao = taskDao)

        val result = repository.getById("missing")

        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        val error = (result as DomainResult.Failure).error
        assertThat(error).isInstanceOf(StorageException.NotFound::class)
        val notFound = error as StorageException.NotFound
        assertThat(notFound.entity).isEqualTo("Task")
        assertThat(notFound.id).isEqualTo("missing")
    }

    @Test
    fun `get upcoming maps dao items and forwards limit`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            upcomingResult = listOf(
                TaskFixtures.teacherEntity(id = "task-a"),
                TaskFixtures.teacherEntity(id = "task-b", title = "Prepare lecture"),
            )
        }
        val repository = createRepository(taskDao = taskDao)

        val result = repository.getUpcoming("teacher-1", TaskRole.Teacher, 5)

        assertThat(result).isEqualTo(
            DomainResult.Success(
                listOf(
                    TaskFixtures.teacherTask(id = "task-a"),
                    TaskFixtures.teacherTask(id = "task-b", title = "Prepare lecture"),
                ),
            ),
        )
        assertThat(taskDao.lastUpcomingOwnerUserId).isEqualTo("teacher-1")
        assertThat(taskDao.lastUpcomingRole).isEqualTo(TaskRole.Teacher.name)
        assertThat(taskDao.lastUpcomingLimit).isEqualTo(5)
        assertThat(taskDao.lastUpcomingNow).isNotNull()
    }

    @Test
    fun `upsert with blank id calls create and caches api response`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao()
        val api = FakeTasksApi().apply {
            createResponse = TaskFixtures.studentDto(id = "created-7", title = "Server task")
        }
        val repository = createRepository(taskDao = taskDao, api = api)
        val draft = TaskFixtures.studentTask(id = "", title = "Local draft")

        val result = repository.upsert(draft)

        assertThat(result).isEqualTo(
            DomainResult.Success(TaskFixtures.studentTask(id = "created-7", title = "Server task")),
        )
        assertThat(api.createCallCount).isEqualTo(1)
        assertThat(api.updateCallCount).isEqualTo(0)
        assertThat(api.lastCreateRequest?.title).isEqualTo("Local draft")
        assertThat(taskDao.upsertCallCount).isEqualTo(1)
        assertThat(taskDao.lastUpsertItem).isEqualTo(TaskFixtures.studentEntity(id = "created-7", title = "Server task"))
    }

    @Test
    fun `upsert with existing id calls update and caches api response`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao()
        val api = FakeTasksApi().apply {
            updateResponse = TaskFixtures.teacherDto(id = "task-77", title = "Updated on server")
        }
        val repository = createRepository(taskDao = taskDao, api = api)
        val task = TaskFixtures.teacherTask(id = "task-77", title = "Before update")

        val result = repository.upsert(task)

        assertThat(result).isEqualTo(
            DomainResult.Success(TaskFixtures.teacherTask(id = "task-77", title = "Updated on server")),
        )
        assertThat(api.createCallCount).isEqualTo(0)
        assertThat(api.updateCallCount).isEqualTo(1)
        assertThat(api.lastUpdateId).isEqualTo("task-77")
        assertThat(taskDao.upsertCallCount).isEqualTo(1)
        assertThat(taskDao.lastUpsertItem).isEqualTo(
            TaskFixtures.teacherEntity(id = "task-77", title = "Updated on server"),
        )
    }

    @Test
    fun `toggle completion returns not found when local cache misses task`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            getByIdResult = null
        }
        val api = FakeTasksApi()
        val repository = createRepository(taskDao = taskDao, api = api)

        val result = repository.toggleCompletion("missing")

        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        val error = (result as DomainResult.Failure).error
        assertThat(error).isInstanceOf(StorageException.NotFound::class)
        val notFound = error as StorageException.NotFound
        assertThat(notFound.entity).isEqualTo("Task")
        assertThat(notFound.id).isEqualTo("missing")
        assertThat(api.toggleCallCount).isEqualTo(0)
        assertThat(taskDao.upsertCallCount).isEqualTo(0)
    }

    @Test
    fun `toggle completion maps api response using cached owner and stores updated task`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao().apply {
            getByIdResult = TaskFixtures.studentEntity(id = "task-9", ownerUserId = "student-9")
        }
        val api = FakeTasksApi().apply {
            toggleResponse = TaskFixtures.studentDto(
                id = "task-9",
                isCompleted = true,
                completedAt = "2026-05-26T11:00:00",
            )
        }
        val repository = createRepository(taskDao = taskDao, api = api)

        val result = repository.toggleCompletion("task-9")

        assertThat(result).isEqualTo(
            DomainResult.Success(
                TaskFixtures.studentTask(
                    id = "task-9",
                    ownerUserId = "student-9",
                    isCompleted = true,
                    completedAt = TaskFixtures.dateTime("2026-05-26T11:00:00"),
                ),
            ),
        )
        assertThat(api.lastToggleId).isEqualTo("task-9")
        assertThat(taskDao.lastUpsertItem).isEqualTo(
            TaskFixtures.studentEntity(
                id = "task-9",
                ownerUserId = "student-9",
                isCompleted = true,
                completedAt = TaskFixtures.dateTime("2026-05-26T11:00:00"),
            ),
        )
    }

    @Test
    fun `delete removes task remotely and locally`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao()
        val api = FakeTasksApi()
        val repository = createRepository(taskDao = taskDao, api = api)

        val result = repository.delete("task-3")

        assertThat(result).isEqualTo(DomainResult.Success(Unit))
        assertThat(api.deleteCallCount).isEqualTo(1)
        assertThat(api.lastDeleteId).isEqualTo("task-3")
        assertThat(taskDao.deleteByIdCallCount).isEqualTo(1)
        assertThat(taskDao.lastDeleteById).isEqualTo("task-3")
    }

    @Test
    fun `refresh stores mapped tasks for requested role`() = runTest(dispatcher) {
        val taskDao = FakeTaskDao()
        val api = FakeTasksApi().apply {
            listResponse = TaskFixtures.taskListResponse(
                listOf(
                    TaskFixtures.teacherDto(id = "task-21"),
                    TaskFixtures.teacherDto(id = "task-22", title = "Prepare exam"),
                ),
            )
        }
        val repository = createRepository(taskDao = taskDao, api = api)

        val result = repository.refresh("teacher-1", TaskRole.Teacher)

        assertThat(result).isEqualTo(DomainResult.Success(Unit))
        assertThat(api.lastListRole).isEqualTo(TaskRole.Teacher.name)
        assertThat(taskDao.upsertAllCallCount).isEqualTo(1)
        assertThat(taskDao.lastUpsertAllItems).isEqualTo(
            listOf(
                TaskFixtures.teacherEntity(id = "task-21", ownerUserId = "teacher-1"),
                TaskFixtures.teacherEntity(id = "task-22", ownerUserId = "teacher-1", title = "Prepare exam"),
            ),
        )
    }

    @Test
    fun `api failures are mapped through HttpErrorMapper`() = runTest(dispatcher) {
        val api = FakeTasksApi().apply {
            createThrowable = UnknownHostException("offline")
        }
        val repository = createRepository(api = api)

        val result = repository.upsert(TaskFixtures.studentTask(id = ""))

        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        val error = (result as DomainResult.Failure).error
        assertThat(error).isInstanceOf(NetworkException.NoConnection::class)
    }

    private fun createRepository(
        taskDao: FakeTaskDao = FakeTaskDao(),
        api: FakeTasksApi = FakeTasksApi(),
    ): TaskRepositoryImpl = TaskRepositoryImpl(
        taskDao = taskDao,
        api = api,
        errorMapper = HttpErrorMapper(),
        dispatchers = dispatchers,
    )
}
