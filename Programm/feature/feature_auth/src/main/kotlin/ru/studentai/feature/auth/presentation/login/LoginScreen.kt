package ru.studentai.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@Composable
public fun LoginScreen(
    onLoggedIn: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            LoginEffect.NavigateHome -> onLoggedIn()
            LoginEffect.NavigateToRegister -> onNavigateToRegister()
            is LoginEffect.ShowError -> {
                // Текст ошибки уже отрендерится через state.errorMessage —
                // отдельная Snackbar-логика подключится при наличии SnackbarController.
            }
        }
    }

    LoginContent(
        state = state,
        onEvent = viewModel::dispatch,
    )
}

@Composable
internal fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
) {
    ScreenScaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = StudentAiTheme.spacing.lg)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(StudentAiTheme.spacing.xxl))

            Text(
                text = stringResource(R.string.feature_auth_login_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xs))
            Text(
                text = stringResource(R.string.feature_auth_login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xl))

            AppTextField(
                value = state.email,
                onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
                label = stringResource(R.string.feature_auth_login_email_label),
                errorMessage = state.emailError,
                keyboardOptions = AppTextFieldDefaults.EmailKeyboardOptions,
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.md))

            AppPasswordField(
                value = state.password,
                onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                label = stringResource(R.string.feature_auth_login_password_label),
                errorMessage = state.passwordError,
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
                text = stringResource(R.string.feature_auth_login_submit),
                onClick = { onEvent(LoginEvent.SubmitClicked) },
                modifier = Modifier.fillMaxWidth(),
                loading = state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.sm))

            TertiaryButton(
                text = stringResource(R.string.feature_auth_login_forgot_password),
                onClick = { onEvent(LoginEvent.ForgotPasswordClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.sm))

            TertiaryButton(
                text = stringResource(R.string.feature_auth_login_no_account),
                onClick = { onEvent(LoginEvent.NavigateToRegisterClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xxl))
        }
    }
}
