package ru.studentai.feature.home.presentation.home

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.home.domain.usecase.GetHomeSnapshotUseCase

@HiltViewModel
public class HomeViewModel @Inject constructor(
    private val getSnapshot: GetHomeSnapshotUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>(
    initialState = HomeState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    init {
        dispatch(HomeEvent.Load)
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Load, HomeEvent.RetryClicked -> load(initial = currentState.snapshot is ContentState.Idle)
            HomeEvent.Refresh -> load(initial = false, isRefresh = true)
            HomeEvent.ProfileClicked -> sendEffect(HomeEffect.NavigateToProfile)
            is HomeEvent.QuickActionClicked -> sendEffect(HomeEffect.NavigateQuickAction(event.action))
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(HomeEffect.ShowError(errorResolver.resolve(error)))
    }

    private fun load(initial: Boolean, isRefresh: Boolean = false) {
        updateState {
            it.copy(
                snapshot = if (initial) ContentState.Loading else it.snapshot,
                isRefreshing = isRefresh,
            )
        }
        launchSafe {
            val result = getSnapshot()
            updateState { it.copy(snapshot = ContentState.from(result), isRefreshing = false) }
        }
    }
}
