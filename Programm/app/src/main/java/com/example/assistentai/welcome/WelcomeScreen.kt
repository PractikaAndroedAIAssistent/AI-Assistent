package com.example.assistentai.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.component.layout.AppHorizontalDivider
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole

@Composable
public fun WelcomeScreen(
    onLoggedOut: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            WelcomeEffect.NavigateToLogin -> onLoggedOut()
            is WelcomeEffect.ShowError -> Unit
        }
    }

    ScreenScaffold { padding ->
        when (val content = state.profile) {
            ContentState.Idle, ContentState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(padding),
                message = "Загружаем профиль…",
            )
            ContentState.Empty -> Unit
            is ContentState.Error -> ErrorState(
                title = "Не удалось загрузить профиль",
                message = content.error.message ?: "—",
                modifier = Modifier.fillMaxSize().padding(padding),
                onRetry = { viewModel.dispatch(WelcomeEvent.RetryClicked) },
            )
            is ContentState.Success -> WelcomeBody(
                profile = content.data,
                isLoggingOut = state.isLoggingOut,
                modifier = Modifier.fillMaxSize().padding(padding),
                onLogout = { viewModel.dispatch(WelcomeEvent.LogoutClicked) },
            )
        }
    }
}

@Composable
private fun WelcomeBody(
    profile: UserProfile,
    isLoggingOut: Boolean,
    modifier: Modifier,
    onLogout: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = StudentAiTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.xxl))

        Icon(
            imageVector = StudentAiIcons.Success,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        Text(
            text = "Вход выполнен",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xs))
        Text(
            text = "Архитектура работает, можно подключать остальные фичи.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xl))

        AppCard(
            style = AppCardStyle.Outlined,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ProfileRow(label = "ФИО", value = profile.user.fullName)
            AppHorizontalDivider()
            ProfileRow(label = "Email", value = profile.user.email)
            AppHorizontalDivider()
            ProfileRow(
                label = "Роль",
                value = when (profile.user.role) {
                    UserRole.Student -> "Студент"
                    UserRole.Teacher -> "Преподаватель"
                },
            )
            profile.university?.let {
                AppHorizontalDivider()
                ProfileRow(label = "Учебное заведение", value = it)
            }
            profile.group?.let {
                AppHorizontalDivider()
                ProfileRow(label = "Группа", value = it)
            }
            profile.course?.let {
                AppHorizontalDivider()
                ProfileRow(label = "Курс", value = it.toString())
            }
            profile.speciality?.let {
                AppHorizontalDivider()
                ProfileRow(label = "Специальность", value = it)
            }
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.xl))

        PrimaryButton(
            text = "Выйти",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            loading = isLoggingOut,
            leadingIcon = StudentAiIcons.Logout,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xxl))
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = StudentAiTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
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
