package ru.studentai.tests.schedule

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.usecase.DeleteScheduleItemUseCase
import ru.studentai.feature.schedule.domain.usecase.ImportFromUniversityUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveScheduleForDayUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveScheduleForWeekUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveSubjectsUseCase
import ru.studentai.feature.schedule.domain.usecase.RefreshScheduleUseCase
import ru.studentai.feature.schedule.presentation.schedule.ScheduleEffect
import ru.studentai.feature.schedule.presentation.schedule.ScheduleEvent
import ru.studentai.feature.schedule.presentation.schedule.ScheduleMode
import ru.studentai.feature.schedule.presentation.schedule.ScheduleViewModel
import ru.studentai.tests.schedule.support.FakeAuthRepository
import ru.studentai.tests.schedule.support.FakeScheduleRepository
import ru.studentai.tests.schedule.support.ScheduleFixtures
import ru.studentai.tests.schedule.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {

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
    fun `init loads day items and subjects into success state`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(ScheduleFixtures.studentProfile())
        }
        val scheduleRepository = FakeScheduleRepository().apply {
            dayFlow.value = listOf(ScheduleFixtures.scheduleItem(subjectName = "Math"))
            subjectsFlow.value = listOf(ScheduleFixtures.subject(name = "Math"))
        }

        val viewModel = createViewModel(authRepository, scheduleRepository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.items).isEqualTo(
            ContentState.Success(listOf(ScheduleFixtures.scheduleItem(subjectName = "Math"))),
        )
        assertThat(viewModel.state.value.subjects).isEqualTo(listOf(ScheduleFixtures.subject(name = "Math")))
        assertThat(scheduleRepository.dayCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastDayOwnerUserId).isEqualTo("student-1")
        assertThat(scheduleRepository.lastDayFilter).isEqualTo(ScheduleFilter.NONE)
    }

    @Test
    fun `refresh keeps current items visible and toggles refreshing flag`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository().apply {
            dayFlow.value = listOf(ScheduleFixtures.scheduleItem(subjectName = "Algorithms"))
            refreshResult = DomainResult.Success(Unit)
        }
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)
        advanceUntilIdle()

        viewModel.dispatch(ScheduleEvent.Refresh)

        assertThat(viewModel.state.value.items).isEqualTo(
            ContentState.Success(listOf(ScheduleFixtures.scheduleItem(subjectName = "Algorithms"))),
        )
        assertThat(viewModel.state.value.isRefreshing).isEqualTo(true)
        advanceUntilIdle()
        assertThat(viewModel.state.value.isRefreshing).isFalse()
        assertThat(scheduleRepository.refreshCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastRefreshOwnerUserId).isEqualTo("student-1")
    }

    @Test
    fun `import toggles flag and calls repository for current user`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository().apply {
            importResult = DomainResult.Success(5)
        }
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)
        advanceUntilIdle()

        viewModel.dispatch(ScheduleEvent.ImportFromUniversityClicked)

        assertThat(viewModel.state.value.isImporting).isEqualTo(true)
        advanceUntilIdle()
        assertThat(viewModel.state.value.isImporting).isFalse()
        assertThat(scheduleRepository.importCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastImportOwnerUserId).isEqualTo("student-1")
    }

    @Test
    fun `mode change to week uses monday of selected date`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository()
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)
        advanceUntilIdle()

        viewModel.dispatch(ScheduleEvent.DateSelected(LocalDate.parse("2026-05-20")))
        viewModel.dispatch(ScheduleEvent.ModeChanged(ScheduleMode.Week))
        advanceUntilIdle()

        assertThat(viewModel.state.value.mode).isEqualTo(ScheduleMode.Week)
        assertThat(scheduleRepository.weekCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastWeekStart).isEqualTo(LocalDate.parse("2026-05-18"))
    }

    @Test
    fun `filter change restarts day observation with selected subject`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository()
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)
        advanceUntilIdle()

        viewModel.dispatch(ScheduleEvent.FilterSubjectChanged("subject-7"))
        advanceUntilIdle()

        assertThat(scheduleRepository.dayCallCount).isEqualTo(2)
        assertThat(scheduleRepository.lastDayFilter).isEqualTo(ScheduleFilter(subjectId = "subject-7"))
    }

    @Test
    fun `add lesson click emits navigation effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.dispatch(ScheduleEvent.AddLessonClicked)
            assertThat(awaitItem()).isEqualTo(ScheduleEffect.NavigateToAddLesson)
        }
    }

    @Test
    fun `delete failure resolves message and emits error effect`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository().apply {
            deleteResult = DomainResult.Failure(StorageException.NotFound(entity = "ScheduleItem", id = "missing"))
        }
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatch(ScheduleEvent.DeleteLessonRequested("missing"))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(ScheduleEffect.ShowMessage("resolved"))
        }

        assertThat(scheduleRepository.deleteCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastDeleteId).isEqualTo("missing")
    }

    private fun createViewModel(
        authRepository: FakeAuthRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(ScheduleFixtures.studentProfile())
        },
        scheduleRepository: FakeScheduleRepository = FakeScheduleRepository(),
    ): ScheduleViewModel = ScheduleViewModel(
        getProfile = GetProfileUseCase(authRepository),
        observeDay = ObserveScheduleForDayUseCase(scheduleRepository),
        observeWeek = ObserveScheduleForWeekUseCase(scheduleRepository),
        observeSubjects = ObserveSubjectsUseCase(scheduleRepository),
        deleteLesson = DeleteScheduleItemUseCase(scheduleRepository),
        importFromUniversity = ImportFromUniversityUseCase(scheduleRepository),
        refresh = RefreshScheduleUseCase(scheduleRepository),
        errorResolver = ErrorMessageResolver { "resolved" },
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
