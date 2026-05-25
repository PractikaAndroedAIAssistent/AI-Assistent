package ru.studentai.tests.tasks

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.usecase.DeleteTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTaskSubjectsUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTasksUseCase
import ru.studentai.feature.tasks.domain.usecase.ToggleTaskCompletionUseCase
import ru.studentai.feature.tasks.presentation.list.TaskListFilterPreset
import ru.studentai.feature.tasks.presentation.list.TasksEffect
import ru.studentai.feature.tasks.presentation.list.TasksEvent
import ru.studentai.feature.tasks.presentation.list.TasksViewModel
import ru.studentai.tests.tasks.support.FakeAuthRepository
import ru.studentai.tests.tasks.support.FakeTaskRepository
import ru.studentai.tests.tasks.support.TaskFixtures
import ru.studentai.tests.tasks.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = TestDispatcherProvider(dispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads student tasks and subjects into success state`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(TaskFixtures.studentProfile())
        }
        val taskRepository = FakeTaskRepository().apply {
            tasksFlow.value = listOf(TaskFixtures.studentTask(title = "Lab 1"))
            subjectsFlow.value = listOf("Algorithms")
        }

        val viewModel = createViewModel(authRepository, taskRepository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.role).isEqualTo(TaskRole.Student)
        assertThat(viewModel.state.value.items).isEqualTo(
            ContentState.Success(listOf(TaskFixtures.studentTask(title = "Lab 1"))),
        )
        assertThat(viewModel.state.value.subjects).isEqualTo(listOf("Algorithms"))
        assertThat(taskRepository.lastObserveOwnerUserId).isEqualTo("student-1")
        assertThat(taskRepository.lastObserveRole).isEqualTo(TaskRole.Student)
        assertThat(taskRepository.lastObserveFilter).isEqualTo(
            TaskFilter(subjectId = null, showCompleted = false, onlyOverdue = false),
        )
    }

    @Test
    fun `teacher profile switches observation to teacher role`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(TaskFixtures.teacherProfile())
        }
        val taskRepository = FakeTaskRepository().apply {
            tasksFlow.value = listOf(TaskFixtures.teacherTask())
            subjectsFlow.value = listOf("Physics")
        }

        val viewModel = createViewModel(authRepository, taskRepository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.role).isEqualTo(TaskRole.Teacher)
        assertThat(taskRepository.lastObserveRole).isEqualTo(TaskRole.Teacher)
        assertThat(taskRepository.lastSubjectsRole).isEqualTo(TaskRole.Teacher)
    }

    @Test
    fun `filter preset change restarts observe with overdue filter`() = runTest(dispatcher) {
        val taskRepository = FakeTaskRepository()
        val viewModel = createViewModel(taskRepository = taskRepository)
        advanceUntilIdle()

        viewModel.dispatch(TasksEvent.FilterPresetChanged(TaskListFilterPreset.Overdue))
        advanceUntilIdle()

        assertThat(viewModel.state.value.preset).isEqualTo(TaskListFilterPreset.Overdue)
        assertThat(taskRepository.observeTasksCallCount).isEqualTo(2)
        assertThat(taskRepository.lastObserveFilter).isEqualTo(
            TaskFilter(subjectId = null, showCompleted = false, onlyOverdue = true),
        )
    }

    @Test
    fun `subject filter change restarts observe with selected subject`() = runTest(dispatcher) {
        val taskRepository = FakeTaskRepository()
        val viewModel = createViewModel(taskRepository = taskRepository)
        advanceUntilIdle()

        viewModel.dispatch(TasksEvent.SubjectFilterChanged("Algorithms"))
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjectFilter).isEqualTo("Algorithms")
        assertThat(taskRepository.observeTasksCallCount).isEqualTo(2)
        assertThat(taskRepository.lastObserveFilter).isEqualTo(
            TaskFilter(subjectId = "Algorithms", showCompleted = false, onlyOverdue = false),
        )
    }

    @Test
    fun `completed preset keeps only completed items in state`() = runTest(dispatcher) {
        val completed = TaskFixtures.studentTask(
            id = "done",
            isCompleted = true,
            completedAt = TaskFixtures.dateTime("2026-05-26T11:00:00"),
        )
        val active = TaskFixtures.studentTask(id = "todo", title = "Essay")
        val taskRepository = FakeTaskRepository().apply {
            tasksFlow.value = listOf(completed, active)
        }
        val viewModel = createViewModel(taskRepository = taskRepository)
        advanceUntilIdle()

        viewModel.dispatch(TasksEvent.FilterPresetChanged(TaskListFilterPreset.Completed))
        advanceUntilIdle()

        assertThat(viewModel.state.value.items).isEqualTo(ContentState.Success(listOf(completed)))
    }

    @Test
    fun `add click emits navigation effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.dispatch(TasksEvent.AddClicked)
            assertThat(awaitItem()).isEqualTo(TasksEffect.NavigateToAdd)
        }
    }

    @Test
    fun `task click emits navigation to edit effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.dispatch(TasksEvent.TaskClicked("task-12"))
            assertThat(awaitItem()).isEqualTo(TasksEffect.NavigateToEdit("task-12"))
        }
    }

    @Test
    fun `toggle failure resolves message and emits effect`() = runTest(dispatcher) {
        val taskRepository = FakeTaskRepository().apply {
            toggleResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "missing"))
        }
        val viewModel = createViewModel(taskRepository = taskRepository)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatch(TasksEvent.ToggleCompletion("missing"))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(TasksEffect.ShowMessage("resolved"))
        }

        assertThat(taskRepository.toggleCallCount).isEqualTo(1)
        assertThat(taskRepository.lastToggleId).isEqualTo("missing")
    }

    @Test
    fun `delete failure resolves message and emits effect`() = runTest(dispatcher) {
        val taskRepository = FakeTaskRepository().apply {
            deleteResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "missing"))
        }
        val viewModel = createViewModel(taskRepository = taskRepository)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatch(TasksEvent.Delete("missing"))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(TasksEffect.ShowMessage("resolved"))
        }

        assertThat(taskRepository.deleteCallCount).isEqualTo(1)
        assertThat(taskRepository.lastDeleteId).isEqualTo("missing")
    }

    private fun createViewModel(
        authRepository: FakeAuthRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(TaskFixtures.studentProfile())
        },
        taskRepository: FakeTaskRepository = FakeTaskRepository(),
    ): TasksViewModel = TasksViewModel(
        getProfile = GetProfileUseCase(authRepository),
        observeTasks = ObserveTasksUseCase(taskRepository),
        observeSubjects = ObserveTaskSubjectsUseCase(taskRepository),
        toggleCompletion = ToggleTaskCompletionUseCase(taskRepository),
        deleteTask = DeleteTaskUseCase(taskRepository),
        errorResolver = ErrorMessageResolver { "resolved" },
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
