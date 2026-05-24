package ru.studentai.feature.flashcards.domain.model

import androidx.compose.runtime.Immutable

/**
 * Сессия повторения одного набора. ViewModel держит её в state и обновляет
 * по мере прохождения карточек. Сама сессия не сохраняется в БД — карточки
 * обновляются в Room атомарно через [Sm2Algorithm].
 */
@Immutable
public data class ReviewSession(
    public val setId: String,
    public val total: Int,
    public val completed: Int,
    public val currentCard: Flashcard?,
) {
    public val progress: Float
        get() = if (total == 0) 0f else completed.toFloat() / total
    public val isFinished: Boolean
        get() = currentCard == null
}
