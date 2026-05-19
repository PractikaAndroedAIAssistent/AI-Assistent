package com.example.assistentai.welcome

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.auth.domain.model.UserProfile

@Immutable
public data class WelcomeState(
    val profile: ContentState<UserProfile> = ContentState.Idle,
    val isLoggingOut: Boolean = false,
) : UiState

public sealed interface WelcomeEvent : UiEvent {
    public data object LoadProfile : WelcomeEvent
    public data object RetryClicked : WelcomeEvent
    public data object LogoutClicked : WelcomeEvent
}

public sealed interface WelcomeEffect : UiEffect {
    public data object NavigateToLogin : WelcomeEffect
    public data class ShowError(val message: String) : WelcomeEffect
}
