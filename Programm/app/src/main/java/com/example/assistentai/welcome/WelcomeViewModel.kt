package com.example.assistentai.welcome

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.auth.domain.usecase.LogoutUseCase

@HiltViewModel
public class WelcomeViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val logout: LogoutUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<WelcomeState, WelcomeEvent, WelcomeEffect>(
    initialState = WelcomeState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    init {
        dispatch(WelcomeEvent.LoadProfile)
    }

    override fun handleEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.LoadProfile, WelcomeEvent.RetryClicked -> loadProfile()
            WelcomeEvent.LogoutClicked -> performLogout()
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(WelcomeEffect.ShowError(errorResolver.resolve(error)))
    }

    private fun loadProfile() {
        updateState { it.copy(profile = ContentState.Loading) }
        launchSafe {
            val result = getProfile()
            updateState { it.copy(profile = ContentState.from(result)) }
        }
    }

    private fun performLogout() {
        updateState { it.copy(isLoggingOut = true) }
        launchSafe {
            when (val result = logout()) {
                is DomainResult.Success -> {
                    updateState { it.copy(isLoggingOut = false) }
                    sendEffect(WelcomeEffect.NavigateToLogin)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isLoggingOut = false) }
                    sendEffect(WelcomeEffect.ShowError(errorResolver.resolve(result.error)))
                }
            }
        }
    }
}
