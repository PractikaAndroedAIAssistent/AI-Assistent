package ru.studentai.feature.home.presentation.home

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.model.QuickAction

@Immutable
public data class HomeState(
    val snapshot: ContentState<HomeSnapshot> = ContentState.Idle,
    val isRefreshing: Boolean = false,
) : UiState

public sealed interface HomeEvent : UiEvent {
    public data object Load : HomeEvent
    public data object Refresh : HomeEvent
    public data object RetryClicked : HomeEvent
    public data object ProfileClicked : HomeEvent
    public data class QuickActionClicked(val action: QuickAction) : HomeEvent
}

public sealed interface HomeEffect : UiEffect {
    public data object NavigateToProfile : HomeEffect
    public data class NavigateQuickAction(val action: QuickAction) : HomeEffect
    public data class ShowError(val message: String) : HomeEffect
}
