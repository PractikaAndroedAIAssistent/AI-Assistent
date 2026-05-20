package ru.studentai.feature.auth.presentation.login

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState

@Immutable
public data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) : UiState

public sealed interface LoginEvent : UiEvent {
    public data class EmailChanged(val value: String) : LoginEvent
    public data class PasswordChanged(val value: String) : LoginEvent
    public data object SubmitClicked : LoginEvent
    public data object NavigateToRegisterClicked : LoginEvent
    public data object ForgotPasswordClicked : LoginEvent
    public data object ErrorBannerDismissed : LoginEvent
}

public sealed interface LoginEffect : UiEffect {
    public data object NavigateHome : LoginEffect
    public data object NavigateToRegister : LoginEffect
    public data class ShowError(val message: String) : LoginEffect
}
