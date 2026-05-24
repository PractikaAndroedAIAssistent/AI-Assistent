package ru.studentai.feature.flashcards.presentation.edit

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.flashcards.domain.model.Flashcard

@Immutable
public data class SetEditState(
    val setId: String? = null,
    val name: String = "",
    val subjectName: String = "",
    val cards: List<Flashcard> = emptyList(),
    val newCardFront: String = "",
    val newCardBack: String = "",
    val nameError: String? = null,
    val cardError: String? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
) : UiState

public sealed interface SetEditEvent : UiEvent {
    public data class Init(val setId: String?) : SetEditEvent
    public data class NameChanged(val value: String) : SetEditEvent
    public data class SubjectChanged(val value: String) : SetEditEvent
    public data class NewCardFrontChanged(val value: String) : SetEditEvent
    public data class NewCardBackChanged(val value: String) : SetEditEvent
    public data object AddCardClicked : SetEditEvent
    public data class DeleteCardClicked(val cardId: String) : SetEditEvent
    public data object SaveClicked : SetEditEvent
    public data object CancelClicked : SetEditEvent
}

public sealed interface SetEditEffect : UiEffect {
    public data object Saved : SetEditEffect
    public data object Cancelled : SetEditEffect
    public data class ShowMessage(val message: String) : SetEditEffect
}
