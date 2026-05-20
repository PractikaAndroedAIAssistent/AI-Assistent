package ru.studentai.feature.auth.presentation.profile

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.auth.domain.model.UserProfile

@Immutable
public data class ProfileState(
    val content: ContentState<UserProfile> = ContentState.Idle,
    val isEditing: Boolean = false,
    val draft: ProfileDraft = ProfileDraft(),
    val isSaving: Boolean = false,
    val isLoggingOut: Boolean = false,
) : UiState

@Immutable
public data class ProfileDraft(
    val fullName: String = "",
    val university: String = "",
    val group: String = "",
    val course: String = "",
    val speciality: String = "",
)

public sealed interface ProfileEvent : UiEvent {
    public data object LoadProfile : ProfileEvent
    public data object EditClicked : ProfileEvent
    public data object CancelEditClicked : ProfileEvent
    public data object SaveClicked : ProfileEvent
    public data class FullNameChanged(val value: String) : ProfileEvent
    public data class UniversityChanged(val value: String) : ProfileEvent
    public data class GroupChanged(val value: String) : ProfileEvent
    public data class CourseChanged(val value: String) : ProfileEvent
    public data class SpecialityChanged(val value: String) : ProfileEvent
    public data object LogoutClicked : ProfileEvent
    public data object RetryClicked : ProfileEvent
}

public sealed interface ProfileEffect : UiEffect {
    public data object LoggedOut : ProfileEffect
    public data class ShowError(val message: String) : ProfileEffect
    public data object ShowSavedToast : ProfileEffect
}
