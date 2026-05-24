package ru.studentai.feature.flashcards.presentation.sets

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.flashcards.domain.model.FlashcardSet

@Immutable
public data class SetsState(
    val sets: ContentState<List<FlashcardSet>> = ContentState.Idle,
) : UiState

public sealed interface SetsEvent : UiEvent {
    public data object RetryClicked : SetsEvent
    public data class SetClicked(val setId: String) : SetsEvent
    public data class StudyClicked(val setId: String) : SetsEvent
    public data class DeleteSet(val setId: String) : SetsEvent
    public data object AddSetClicked : SetsEvent
}

public sealed interface SetsEffect : UiEffect {
    public data object NavigateToAddSet : SetsEffect
    public data class NavigateToEditSet(val setId: String) : SetsEffect
    public data class NavigateToStudy(val setId: String) : SetsEffect
    public data class ShowMessage(val message: String) : SetsEffect
}
