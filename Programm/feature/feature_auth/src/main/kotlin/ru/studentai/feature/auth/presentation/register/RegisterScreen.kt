package ru.studentai.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.TertiaryButton
import ru.studentai.core.designsystem.component.input.AppPasswordField
import ru.studentai.core.designsystem.component.input.AppTextField
import ru.studentai.core.designsystem.component.input.AppTextFieldDefaults
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.feature.auth.R
import ru.studentai.feature.auth.domain.model.UserRole

@Composable
public fun RegisterScreen(
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            RegisterEffect.NavigateHome -> onRegistered()
            RegisterEffect.NavigateToLogin -> onNavigateToLogin()
            is RegisterEffect.ShowError -> Unit
            RegisterEffect.ShowRegisteredToast -> Unit
        }
    }

    RegisterContent(state = state, onEvent = viewModel::dispatch)
}

@Composable
internal fun RegisterContent(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
) {
    ScreenScaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = StudentAiTheme.spacing.lg)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(StudentAiTheme.spacing.xl))

            Text(
                text = stringResource(R.string.feature_auth_register_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xs))
            Text(
                text = stringResource(R.string.feature_auth_register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.lg))

            AppTextField(
                value = state.fullName,
                onValueChange = { onEvent(RegisterEvent.FullNameChanged(it)) },
                label = stringResource(R.string.feature_auth_register_full_name_label),
                errorMessage = state.fullNameError,
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.md))

            AppTextField(
                value = state.email,
                onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
                label = stringResource(R.string.feature_auth_register_email_label),
                errorMessage = state.emailError,
                keyboardOptions = AppTextFieldDefaults.EmailKeyboardOptions,
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.md))

            AppPasswordField(
                value = state.password,
                onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                label = stringResource(R.string.feature_auth_register_password_label),
                errorMessage = state.passwordError,
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.md))

            AppPasswordField(
                value = state.passwordRepeat,
                onValueChange = { onEvent(RegisterEvent.PasswordRepeatChanged(it)) },
                label = stringResource(R.string.feature_auth_register_password_repeat_label),
                errorMessage = state.passwordRepeatError,
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.lg))

            RoleSelector(
                selected = state.role,
                enabled = !state.isSubmitting,
                onSelected = { onEvent(RegisterEvent.RoleSelected(it)) },
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.lg))

            AppTextField(
                value = state.university,
                onValueChange = { onEvent(RegisterEvent.UniversityChanged(it)) },
                label = stringResource(R.string.feature_auth_register_university_label),
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.md))

            if (state.role == UserRole.Student) {
                AppTextField(
                    value = state.group,
                    onValueChange = { onEvent(RegisterEvent.GroupChanged(it)) },
                    label = stringResource(R.string.feature_auth_register_group_label),
                    enabled = !state.isSubmitting,
                )
                Spacer(Modifier.height(StudentAiTheme.spacing.md))

                AppTextField(
                    value = state.course,
                    onValueChange = { onEvent(RegisterEvent.CourseChanged(it)) },
                    label = stringResource(R.string.feature_auth_register_course_label),
                    keyboardOptions = AppTextFieldDefaults.NumberKeyboardOptions,
                    enabled = !state.isSubmitting,
                )
                Spacer(Modifier.height(StudentAiTheme.spacing.md))
            }

            AppTextField(
                value = state.speciality,
                onValueChange = { onEvent(RegisterEvent.SpecialityChanged(it)) },
                label = stringResource(R.string.feature_auth_register_speciality_label),
                enabled = !state.isSubmitting,
            )

            if (state.errorMessage != null) {
                Spacer(Modifier.height(StudentAiTheme.spacing.md))
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(StudentAiTheme.spacing.lg))

            PrimaryButton(
                text = stringResource(R.string.feature_auth_register_submit),
                onClick = { onEvent(RegisterEvent.SubmitClicked) },
                modifier = Modifier.fillMaxWidth(),
                loading = state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.sm))

            TertiaryButton(
                text = stringResource(R.string.feature_auth_register_have_account),
                onClick = { onEvent(RegisterEvent.NavigateToLoginClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xxl))
        }
    }
}

@Composable
private fun RoleSelector(
    selected: UserRole,
    enabled: Boolean,
    onSelected: (UserRole) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_auth_register_role_label),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xs))
        RoleOption(
            label = stringResource(R.string.feature_auth_register_role_student),
            selected = selected == UserRole.Student,
            enabled = enabled,
            onClick = { onSelected(UserRole.Student) },
        )
        RoleOption(
            label = stringResource(R.string.feature_auth_register_role_teacher),
            selected = selected == UserRole.Teacher,
            enabled = enabled,
            onClick = { onSelected(UserRole.Teacher) },
        )
    }
}

@Composable
private fun RoleOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = StudentAiTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled)
        Spacer(Modifier.width(StudentAiTheme.spacing.sm))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
