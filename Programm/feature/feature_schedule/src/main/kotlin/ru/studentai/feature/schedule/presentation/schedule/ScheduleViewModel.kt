package ru.studentai.feature.schedule.presentation.schedule

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.usecase.DeleteScheduleItemUseCase
import ru.studentai.feature.schedule.domain.usecase.ImportFromUniversityUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveScheduleForDayUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveScheduleForWeekUseCase
import ru.studentai.feature.schedule.domain.usecase.ObserveSubjectsUseCase
import ru.studentai.feature.schedule.domain.usecase.RefreshScheduleUseCase

@HiltViewModel
public class ScheduleViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val observeDay: ObserveScheduleForDayUseCase,
    private val observeWeek: ObserveScheduleForWeekUseCase,
    private val observeSubjects: ObserveSubjectsUseCase,
    private val deleteLesson: DeleteScheduleItemUseCase,
    private val importFromUniversity: ImportFromUniversityUseCase,
    private val refresh: RefreshScheduleUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<ScheduleState, ScheduleEvent, ScheduleEffect>(
    initialState = ScheduleState(
        anchorDate = kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault()),
    ),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var observeJob: Job? = null
    private var userId: String = ""

    init {
        loadProfileAndObserve()
    }

    override fun handleEvent(event: ScheduleEvent) {
        when (event) {
            ScheduleEvent.Refresh -> refresh()
            ScheduleEvent.RetryClicked -> loadProfileAndObserve()
            is ScheduleEvent.ModeChanged -> {
                updateState { it.copy(mode = event.mode) }
                restartObserve()
            }
            is ScheduleEvent.DateSelected -> {
                updateState { it.copy(anchorDate = event.date) }
                restartObserve()
            }
            ScheduleEvent.TodayClicked -> {
                updateState { it.copy(anchorDate = todayLocal()) }
                restartObserve()
            }
            is ScheduleEvent.FilterSubjectChanged -> {
                updateState { it.copy(filter = ScheduleFilter(subjectId = event.subjectId)) }
                restartObserve()
            }
            ScheduleEvent.ImportFromUniversityClicked -> importNow()
            ScheduleEvent.AddLessonClicked -> sendEffect(ScheduleEffect.NavigateToAddLesson)
            is ScheduleEvent.LessonClicked -> sendEffect(ScheduleEffect.NavigateToEditLesson(event.id))
            is ScheduleEvent.DeleteLessonRequested -> deleteNow(event.id)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(ScheduleEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun loadProfileAndObserve() {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> {
                    userId = profile.value.user.id
                    restartObserve()
                }
                is DomainResult.Failure -> defaultErrorHandler(profile.error)
            }
        }
    }

    private fun restartObserve() {
        observeJob?.cancel()
        val anchor = currentState.anchorDate ?: todayLocal()
        val mode = currentState.mode
        val filter = currentState.filter
        val ownerId = userId.takeIf { it.isNotBlank() } ?: return

        updateState { it.copy(items = ContentState.Loading) }

        val itemsFlow = when (mode) {
            ScheduleMode.Day -> observeDay(ownerId, anchor, filter)
            ScheduleMode.Week -> observeWeek(ownerId, anchor.toMonday(), filter)
        }
        observeJob = combine(itemsFlow, observeSubjects(ownerId)) { items, subjects -> items to subjects }
            .onEach { (items, subjects) ->
                updateState {
                    it.copy(
                        items = if (items.isEmpty()) ContentState.Empty else ContentState.Success(items),
                        subjects = subjects,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refresh() {
        updateState { it.copy(isRefreshing = true) }
        launchSafe {
            val ownerId = userId.takeIf { it.isNotBlank() } ?: return@launchSafe
            when (val r = refresh.invoke(ownerId)) {
                is DomainResult.Success -> updateState { it.copy(isRefreshing = false) }
                is DomainResult.Failure -> {
                    updateState { it.copy(isRefreshing = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun importNow() {
        updateState { it.copy(isImporting = true) }
        launchSafe {
            val ownerId = userId.takeIf { it.isNotBlank() } ?: return@launchSafe
            when (val r = importFromUniversity(ownerId)) {
                is DomainResult.Success -> updateState { it.copy(isImporting = false) }
                is DomainResult.Failure -> {
                    updateState { it.copy(isImporting = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun deleteNow(id: String) {
        launchSafe {
            when (val r = deleteLesson(id)) {
                is DomainResult.Success -> Unit // UI обновится через Flow
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }

    private fun LocalDate.toMonday(): LocalDate {
        val offset = (dayOfWeek.value - kotlinx.datetime.DayOfWeek.MONDAY.value + 7) % 7
        return if (offset == 0) this else this.minus(DatePeriod(days = offset))
    }

    private fun todayLocal(): LocalDate =
        kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault())
}
