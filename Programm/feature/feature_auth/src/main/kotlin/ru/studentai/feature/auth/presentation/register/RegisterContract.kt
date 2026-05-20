package ru.studentai.feature.auth.presentation.register

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.auth.domain.model.UserRole

@Immutable
public data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
    val role: UserRole = UserRole.Student,
    val university: String = "",
    val group: String = "",
    val course: String = "",
    val speciality: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordRepeatError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) : UiState

public sealed interface RegisterEvent : UiEvent {
    public data class FullNameChanged(val value: String) : RegisterEvent
    public data class EmailChanged(val value: String) : RegisterEvent
    public data class PasswordChanged(val value: String) : RegisterEvent
    public data class PasswordRepeatChanged(val value: String) : RegisterEvent
    public data class RoleSelected(val role: UserRole) : RegisterEvent
    public data class UniversityChanged(val value: String) : RegisterEvent
    public data class GroupChanged(val value: String) : RegisterEvent
    public data class CourseChanged(val value: String) : RegisterEvent
    public data class SpecialityChanged(val value: String) : RegisterEvent
    public data object SubmitClicked : RegisterEvent
    public data object NavigateToLoginClicked : RegisterEvent
    public data object ErrorBannerDismissed : RegisterEvent
}

public sealed interface RegisterEffect : UiEffect {
    public data object NavigateHome : RegisterEffect
    public data object NavigateToLogin : RegisterEffect
    public data class ShowError(val message: String) : RegisterEffect
    public data object ShowRegisteredToast : RegisterEffect
}
