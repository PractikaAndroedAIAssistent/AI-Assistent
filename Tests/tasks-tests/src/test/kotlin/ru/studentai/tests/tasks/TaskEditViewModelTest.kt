package ru.studentai.tests.tasks

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
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
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.usecase.GetTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.UpsertTaskUseCase
import ru.studentai.feature.tasks.presentation.edit.TaskEditEffect
import ru.studentai.feature.tasks.presentation.edit.TaskEditEvent
import ru.studentai.feature.tasks.presentation.edit.TaskEditViewModel
import ru.studentai.tests.tasks.support.FakeAuthRepository
import ru.studentai.tests.tasks.support.FakeTaskRepository
import ru.studentai.tests.tasks.support.TaskFixtures
import ru.studentai.tests.tasks.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class TaskEditViewModelTest {

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
    fun `init without item id creates default form for current role`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            authRepository = FakeAuthRepository().apply {
                currentProfileResult = DomainResult.Success(TaskFixtures.studentProfile())
            },
        )

        viewModel.dispatch(TaskEditEvent.Init(itemId = null))
        advanceUntilIdle()

        val form = viewModel.state.value.form
        assertThat(form.itemId).isEqualTo(null)
        assertThat(form.role).isEqualTo(TaskRole.Student)
        assertThat(form.dueDate).isNotNull()
        assertThat(form.dueTime).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `init with existing teacher task loads form fields`() = runTest(dispatcher) {
        val repository = FakeTaskRepository().apply {
            getByIdResult = DomainResult.Success(
                TaskFixtures.teacherTask(
                    id = "task-22",
                    subjectName = "Physics",
                    title = "Check reports",
                    description = "Before seminar",
                    dueAt = TaskFixtures.dateTime("2026-05-28T09:30:00"),
                    priority = TaskPriority.Critical,
                    groupName = "IU7-42B",
                ),
            )
        }
        val viewModel = createViewModel(
            authRepository = FakeAuthRepository().apply {
                currentProfileResult = DomainResult.Success(TaskFixtures.teacherProfile())
            },
            taskRepository = repository,
        )

        viewModel.dispatch(TaskEditEvent.Init(itemId = "task-22"))
        advanceUntilIdle()

        val form = viewModel.state.value.form
        assertThat(form.itemId).isEqualTo("task-22")
        assertThat(form.role).isEqualTo(TaskRole.Teacher)
        assertThat(form.subjectName).isEqualTo("Physics")
        assertThat(form.title).isEqualTo("Check reports")
        assertThat(form.description).isEqualTo("Before seminar")
        assertThat(form.dueDate).isEqualTo(TaskFixtures.date("2026-05-28"))
        assertThat(form.dueTime).isEqualTo(TaskFixtures.time("09:30:00"))
        assertThat(form.priority).isEqualTo(TaskPriority.Critical)
        assertThat(form.groupName).isEqualTo("IU7-42B")
        assertThat(repository.getByIdCallCount).isEqualTo(1)
        assertThat(repository.lastGetByIdId).isEqualTo("task-22")
    }

    @Test
    fun `save with invalid form populates title and due errors and skips upsert`() = runTest(dispatcher) {
        val repository = FakeTaskRepository()
        val viewModel = createViewModel(taskRepository = repository)

        viewModel.dispatch(TaskEditEvent.SaveClicked)

        assertThat(viewModel.state.value.titleError).isNotNull()
        assertThat(viewModel.state.value.dueError).isNotNull()
        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(repository.upsertCallCount).isEqualTo(0)
    }

    @Test
    fun `save with teacher role and blank group populates group error`() = runTest(dispatcher) {
        val repository = FakeTaskRepository()
        val viewModel = createViewModel(
            authRepository = FakeAuthRepository().apply {
                currentProfileResult = DomainResult.Success(TaskFixtures.teacherProfile())
            },
            taskRepository = repository,
        )

        viewModel.dispatch(TaskEditEvent.Init(itemId = null))
        advanceUntilIdle()
        viewModel.dispatch(TaskEditEvent.TitleChanged("Check lab"))
        viewModel.dispatch(TaskEditEvent.DateChanged(TaskFixtures.date("2026-05-29")))
        viewModel.dispatch(TaskEditEvent.TimeChanged(TaskFixtures.time("11:00:00")))
        viewModel.dispatch(TaskEditEvent.SaveClicked)

        assertThat(viewModel.state.value.groupError).isNotNull()
        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(repository.upsertCallCount).isEqualTo(0)
    }

    @Test
    fun `save trims optional fields and emits saved effect on success`() = runTest(dispatcher) {
        val repository = FakeTaskRepository()
        val viewModel = createViewModel(taskRepository = repository)

        viewModel.effects.test {
            viewModel.dispatch(TaskEditEvent.Init(itemId = null))
            advanceUntilIdle()

            viewModel.dispatch(TaskEditEvent.SubjectChanged("  Algorithms  "))
            viewModel.dispatch(TaskEditEvent.TitleChanged("  Lab 1  "))
            viewModel.dispatch(TaskEditEvent.DescriptionChanged("   "))
            viewModel.dispatch(TaskEditEvent.DateChanged(TaskFixtures.date("2026-05-30")))
            viewModel.dispatch(TaskEditEvent.TimeChanged(TaskFixtures.time("14:30:00")))
            viewModel.dispatch(TaskEditEvent.PriorityChanged(TaskPriority.High))
            viewModel.dispatch(TaskEditEvent.SaveClicked)

            assertThat(viewModel.state.value.isSaving).isTrue()
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(TaskEditEffect.Saved)
        }

        val saved = repository.lastUpsertTask
        val deadline = saved as StudyTask.StudentDeadline
        assertThat(deadline.id.isNotBlank()).isTrue()
        assertThat(deadline.ownerUserId).isEqualTo("student-1")
        assertThat(deadline.subjectName).isEqualTo("Algorithms")
        assertThat(deadline.title).isEqualTo("Lab 1")
        assertThat(deadline.description).isNull()
        assertThat(deadline.priority).isEqualTo(TaskPriority.High)
        assertThat(deadline.dueAt).isEqualTo(TaskFixtures.dateTime("2026-05-30T14:30:00"))
        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(repository.upsertCallCount).isEqualTo(1)
    }

    @Test
    fun `save failure resolves message and resets saving flag`() = runTest(dispatcher) {
        val repository = FakeTaskRepository().apply {
            upsertResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "missing"))
        }
        val viewModel = createViewModel(taskRepository = repository)

        viewModel.effects.test {
            viewModel.dispatch(TaskEditEvent.Init(itemId = null))
            advanceUntilIdle()
            viewModel.dispatch(TaskEditEvent.TitleChanged("Essay"))
            viewModel.dispatch(TaskEditEvent.DateChanged(TaskFixtures.date("2026-05-31")))
            viewModel.dispatch(TaskEditEvent.TimeChanged(TaskFixtures.time("10:00:00")))
            viewModel.dispatch(TaskEditEvent.SaveClicked)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(TaskEditEffect.ShowMessage("resolved"))
        }

        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(repository.upsertCallCount).isEqualTo(1)
    }

    @Test
    fun `load failure emits message and leaves loading false`() = runTest(dispatcher) {
        val repository = FakeTaskRepository().apply {
            getByIdResult = DomainResult.Failure(StorageException.NotFound(entity = "Task", id = "missing"))
        }
        val viewModel = createViewModel(taskRepository = repository)

        viewModel.effects.test {
            viewModel.dispatch(TaskEditEvent.Init(itemId = "missing"))
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(TaskEditEffect.ShowMessage("resolved"))
        }

        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `cancel click emits cancelled effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.dispatch(TaskEditEvent.CancelClicked)
            assertThat(awaitItem()).isEqualTo(TaskEditEffect.Cancelled)
        }
    }

    private fun createViewModel(
        authRepository: FakeAuthRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(TaskFixtures.studentProfile())
        },
        taskRepository: FakeTaskRepository = FakeTaskRepository(),
    ): TaskEditViewModel = TaskEditViewModel(
        getProfile = GetProfileUseCase(authRepository),
        getTask = GetTaskUseCase(taskRepository),
        upsert = UpsertTaskUseCase(taskRepository),
        errorResolver = ErrorMessageResolver { "resolved" },
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
