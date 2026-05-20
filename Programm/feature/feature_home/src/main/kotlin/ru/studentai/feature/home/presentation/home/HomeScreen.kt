package ru.studentai.feature.home.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.model.QuickAction
import ru.studentai.feature.home.presentation.home.components.HomeTopBar

/**
 * Главный экран приложения (ТЗ §4.2.2).
 *
 * Диспетчер по содержимому [HomeState.snapshot]:
 *  • Loading/Idle → [LoadingState]
 *  • Error → [ErrorState] с retry
 *  • Success(Student) → [StudentHomeContent]
 *  • Success(Teacher) → [TeacherHomeContent]
 *
 * UI-эффекты:
 *  • [HomeEffect.NavigateToProfile] → [onNavigateToProfile]
 *  • [HomeEffect.NavigateQuickAction] → [onQuickAction] (наружу — навигация в соответствующую feature_*)
 *  • [HomeEffect.ShowError] — на главной экране игнорируется; ошибка рендерится через ErrorState
 */
@Composable
public fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onQuickAction: (QuickAction) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            HomeEffect.NavigateToProfile -> onNavigateToProfile()
            is HomeEffect.NavigateQuickAction -> onQuickAction(effect.action)
            is HomeEffect.ShowError -> Unit
        }
    }

    ScreenScaffold(
        topBar = {
            HomeTopBar(onProfileClick = { viewModel.dispatch(HomeEvent.ProfileClicked) })
        },
    ) { padding ->
        when (val content = state.snapshot) {
            ContentState.Idle, ContentState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(padding),
                message = "Загружаем главную…",
            )
            ContentState.Empty -> Unit
            is ContentState.Error -> ErrorState(
                title = "Не удалось загрузить главную",
                message = content.error.message ?: "—",
                modifier = Modifier.fillMaxSize().padding(padding),
                onRetry = { viewModel.dispatch(HomeEvent.RetryClicked) },
            )
            is ContentState.Success -> when (val snapshot = content.data) {
                is HomeSnapshot.Student -> StudentHomeContent(
                    snapshot = snapshot,
                    contentPadding = padding,
                    onQuickAction = { viewModel.dispatch(HomeEvent.QuickActionClicked(it)) },
                )
                is HomeSnapshot.Teacher -> TeacherHomeContent(
                    snapshot = snapshot,
                    contentPadding = padding,
                    onQuickAction = { viewModel.dispatch(HomeEvent.QuickActionClicked(it)) },
                )
            }
        }
    }
}
