package ru.studentai.feature.flashcards.presentation.study

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.ReviewQuality

@Immutable
public data class StudyState(
    val setId: String? = null,
    val setName: String = "",
    val total: Int = 0,
    val completed: Int = 0,
    val currentCard: Flashcard? = null,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
) : UiState {
    public val progress: Float
        get() = if (total == 0) 0f else completed.toFloat() / total
    public val isFinished: Boolean
        get() = currentCard == null && !isLoading
}

public sealed interface StudyEvent : UiEvent {
    public data class Init(val setId: String) : StudyEvent
    public data object FlipClicked : StudyEvent
    public data class QualitySubmitted(val quality: ReviewQuality) : StudyEvent
    public data object FinishedAcknowledged : StudyEvent
}

public sealed interface StudyEffect : UiEffect {
    public data object Closed : StudyEffect
    public data class ShowMessage(val message: String) : StudyEffect
}
