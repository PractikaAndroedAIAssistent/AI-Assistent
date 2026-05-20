package ru.studentai.feature.auth.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.SecondaryButton
import ru.studentai.core.designsystem.component.button.TertiaryButton
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.input.AppTextField
import ru.studentai.core.designsystem.component.input.AppTextFieldDefaults
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppCenterAlignedTopBar
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.R
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole

@Composable
public fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            ProfileEffect.LoggedOut -> onLoggedOut()
            is ProfileEffect.ShowError -> Unit
            ProfileEffect.ShowSavedToast -> Unit
        }
    }

    ProfileContent(state = state, onEvent = viewModel::dispatch)
}

@Composable
internal fun ProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
) {
    ScreenScaffold(
        topBar = {
            AppCenterAlignedTopBar(
                title = stringResource(R.string.feature_auth_profile_title),
            )
        },
    ) { padding ->
        when (val content = state.content) {
            ContentState.Idle, ContentState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is ContentState.Error -> ErrorState(
                title = stringResource(android.R.string.dialog_alert_title),
                message = "—",
                modifier = Modifier.fillMaxSize().padding(padding),
                onRetry = { onEvent(ProfileEvent.RetryClicked) },
            )
            ContentState.Empty -> Unit
            is ContentState.Success -> ProfileBody(
                profile = content.data,
                state = state,
                modifier = Modifier.fillMaxSize().padding(padding),
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun ProfileBody(
    profile: UserProfile,
    state: ProfileState,
    modifier: Modifier,
    onEvent: (ProfileEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = StudentAiTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        AppCard(style = AppCardStyle.Filled, modifier = Modifier.fillMaxWidth()) {
            if (state.isEditing) {
                EditableProfile(state = state, onEvent = onEvent)
            } else {
                ReadOnlyProfile(profile = profile)
            }
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        if (state.isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
            ) {
                SecondaryButton(
                    text = stringResource(R.string.feature_auth_profile_cancel),
                    onClick = { onEvent(ProfileEvent.CancelEditClicked) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving,
                )
                PrimaryButton(
                    text = stringResource(R.string.feature_auth_profile_save),
                    onClick = { onEvent(ProfileEvent.SaveClicked) },
                    modifier = Modifier.weight(1f),
                    loading = state.isSaving,
                )
            }
        } else {
            PrimaryButton(
                text = stringResource(R.string.feature_auth_profile_edit),
                onClick = { onEvent(ProfileEvent.EditClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoggingOut,
            )
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        TertiaryButton(
            text = stringResource(R.string.feature_auth_profile_logout),
            onClick = { onEvent(ProfileEvent.LogoutClicked) },
            modifier = Modifier.fillMaxWidth(),
            loading = state.isLoggingOut,
            enabled = !state.isSaving,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xl))
    }
}

@Composable
private fun ReadOnlyProfile(profile: UserProfile) {
    Field(label = "ФИО", value = profile.user.fullName)
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    Field(label = "Email", value = profile.user.email)
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    Field(
        label = "Роль",
        value = when (profile.user.role) {
            UserRole.Student -> stringResource(R.string.feature_auth_register_role_student)
            UserRole.Teacher -> stringResource(R.string.feature_auth_register_role_teacher)
        },
    )
    profile.university?.let {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        Field(label = "Учебное заведение", value = it)
    }
    profile.group?.let {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        Field(label = "Группа", value = it)
    }
    profile.course?.let {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        Field(label = "Курс", value = it.toString())
    }
    profile.speciality?.let {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        Field(label = "Специальность", value = it)
    }
}

@Composable
private fun Field(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EditableProfile(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
) {
    val draft = state.draft
    AppTextField(
        value = draft.fullName,
        onValueChange = { onEvent(ProfileEvent.FullNameChanged(it)) },
        label = stringResource(R.string.feature_auth_register_full_name_label),
        enabled = !state.isSaving,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    AppTextField(
        value = draft.university,
        onValueChange = { onEvent(ProfileEvent.UniversityChanged(it)) },
        label = stringResource(R.string.feature_auth_register_university_label),
        enabled = !state.isSaving,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    AppTextField(
        value = draft.group,
        onValueChange = { onEvent(ProfileEvent.GroupChanged(it)) },
        label = stringResource(R.string.feature_auth_register_group_label),
        enabled = !state.isSaving,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    AppTextField(
        value = draft.course,
        onValueChange = { onEvent(ProfileEvent.CourseChanged(it)) },
        label = stringResource(R.string.feature_auth_register_course_label),
        keyboardOptions = AppTextFieldDefaults.NumberKeyboardOptions,
        enabled = !state.isSaving,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    AppTextField(
        value = draft.speciality,
        onValueChange = { onEvent(ProfileEvent.SpecialityChanged(it)) },
        label = stringResource(R.string.feature_auth_register_speciality_label),
        enabled = !state.isSaving,
    )
}
