package ru.studentai.tests.schedule

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.usecase.GetScheduleItemUseCase
import ru.studentai.feature.schedule.domain.usecase.UpsertScheduleItemUseCase
import ru.studentai.feature.schedule.presentation.edit.LessonEditEffect
import ru.studentai.feature.schedule.presentation.edit.LessonEditEvent
import ru.studentai.feature.schedule.presentation.edit.LessonEditViewModel
import ru.studentai.tests.schedule.support.FakeAuthRepository
import ru.studentai.tests.schedule.support.FakeScheduleRepository
import ru.studentai.tests.schedule.support.ScheduleFixtures
import ru.studentai.tests.schedule.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class LessonEditViewModelTest {

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
    fun `init without item id creates default form with date and time`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.dispatch(LessonEditEvent.Init(itemId = null))
        advanceUntilIdle()

        val form = viewModel.state.value.form
        assertThat(form.itemId).isEqualTo(null)
        assertThat(form.date).isNotNull()
        assertThat(form.startTime).isNotNull()
        assertThat(form.endTime).isNotNull()
        assertThat(form.startTime!! < form.endTime!!).isTrue()
        assertThat(viewModel.state.value.loadFailed).isFalse()
    }

    @Test
    fun `init with existing item loads form fields from repository`() = runTest(dispatcher) {
        val item = ScheduleFixtures.scheduleItem(
            id = "lesson-22",
            subjectName = "Databases",
            startAt = ScheduleFixtures.date("2026-05-20").atTime(LocalTime.parse("09:00:00")),
            endAt = ScheduleFixtures.date("2026-05-20").atTime(LocalTime.parse("10:30:00")),
            room = "401",
            teacher = "N.N. Smirnov",
            note = "Bring laptop",
        )
        val scheduleRepository = FakeScheduleRepository().apply {
            getByIdResult = DomainResult.Success(item)
        }
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        viewModel.dispatch(LessonEditEvent.Init(itemId = "lesson-22"))
        advanceUntilIdle()

        assertThat(viewModel.state.value.form.itemId).isEqualTo("lesson-22")
        assertThat(viewModel.state.value.form.subjectName).isEqualTo("Databases")
        assertThat(viewModel.state.value.form.date).isEqualTo(item.startAt.date)
        assertThat(viewModel.state.value.form.startTime).isEqualTo(LocalTime.parse("09:00:00"))
        assertThat(viewModel.state.value.form.endTime).isEqualTo(LocalTime.parse("10:30:00"))
        assertThat(viewModel.state.value.form.room).isEqualTo("401")
        assertThat(viewModel.state.value.form.teacher).isEqualTo("N.N. Smirnov")
        assertThat(viewModel.state.value.form.note).isEqualTo("Bring laptop")
        assertThat(scheduleRepository.getByIdCallCount).isEqualTo(1)
        assertThat(scheduleRepository.lastGetById).isEqualTo("lesson-22")
    }

    @Test
    fun `save with invalid form populates validation errors and skips upsert`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository()
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        viewModel.dispatch(LessonEditEvent.SaveClicked)

        assertThat(viewModel.state.value.subjectError).isNotNull()
        assertThat(viewModel.state.value.timeError).isNotNull()
        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(scheduleRepository.upsertCallCount).isEqualTo(0)
    }

    @Test
    fun `save trims fields and emits saved effect on success`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository()
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        viewModel.effects.test {
            viewModel.dispatch(LessonEditEvent.Init(itemId = null))
            advanceUntilIdle()

            viewModel.dispatch(LessonEditEvent.SubjectChanged("  Math  "))
            viewModel.dispatch(LessonEditEvent.LessonTypeChanged(LessonType.Other))
            viewModel.dispatch(LessonEditEvent.CustomTypeLabelChanged("  Workshop  "))
            viewModel.dispatch(LessonEditEvent.DateChanged(LocalDate.parse("2026-05-21")))
            viewModel.dispatch(LessonEditEvent.StartTimeChanged(LocalTime.parse("11:00:00")))
            viewModel.dispatch(LessonEditEvent.EndTimeChanged(LocalTime.parse("12:30:00")))
            viewModel.dispatch(LessonEditEvent.RoomChanged("  101  "))
            viewModel.dispatch(LessonEditEvent.TeacherChanged("  Dr. Stone  "))
            viewModel.dispatch(LessonEditEvent.NoteChanged("  Bring notebook  "))
            viewModel.dispatch(LessonEditEvent.SaveClicked)

            assertThat(viewModel.state.value.isSaving).isEqualTo(true)
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LessonEditEffect.Saved)

            val saved = scheduleRepository.lastUpsertItem
            assertThat(saved).isNotNull()
            assertThat(saved!!.id.isNotBlank()).isTrue()
            assertThat(saved.ownerUserId).isEqualTo("student-1")
            assertThat(saved.subjectName).isEqualTo("Math")
            assertThat(saved.lessonType).isEqualTo(LessonType.Other)
            assertThat(saved.customTypeLabel).isEqualTo("Workshop")
            assertThat(saved.room).isEqualTo("101")
            assertThat(saved.teacher).isEqualTo("Dr. Stone")
            assertThat(saved.note).isEqualTo("Bring notebook")
            assertThat(saved.startAt.date).isEqualTo(LocalDate.parse("2026-05-21"))
            assertThat(saved.startAt.time).isEqualTo(LocalTime.parse("11:00:00"))
            assertThat(saved.endAt.time).isEqualTo(LocalTime.parse("12:30:00"))
        }

        assertThat(viewModel.state.value.isSaving).isFalse()
        assertThat(scheduleRepository.upsertCallCount).isEqualTo(1)
    }

    @Test
    fun `load failure marks state and emits message`() = runTest(dispatcher) {
        val scheduleRepository = FakeScheduleRepository().apply {
            getByIdResult = DomainResult.Failure(StorageException.NotFound(entity = "ScheduleItem", id = "missing"))
        }
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        viewModel.effects.test {
            viewModel.dispatch(LessonEditEvent.Init(itemId = "missing"))
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(LessonEditEffect.ShowMessage("resolved"))
        }

        assertThat(viewModel.state.value.loadFailed).isTrue()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    private fun createViewModel(
        authRepository: FakeAuthRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(ScheduleFixtures.studentProfile())
        },
        scheduleRepository: FakeScheduleRepository = FakeScheduleRepository(),
    ): LessonEditViewModel = LessonEditViewModel(
        getProfile = GetProfileUseCase(authRepository),
        getItem = GetScheduleItemUseCase(scheduleRepository),
        upsert = UpsertScheduleItemUseCase(scheduleRepository),
        errorResolver = ErrorMessageResolver { "resolved" },
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
