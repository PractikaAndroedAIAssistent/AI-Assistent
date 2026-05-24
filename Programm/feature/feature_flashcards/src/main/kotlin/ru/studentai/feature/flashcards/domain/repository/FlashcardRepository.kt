package ru.studentai.feature.flashcards.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.FlashcardSet
import ru.studentai.feature.flashcards.domain.model.ReviewQuality

public interface FlashcardRepository {

    public fun observeSets(ownerUserId: String): Flow<List<FlashcardSet>>

    public fun observeCardsInSet(setId: String): Flow<List<Flashcard>>

    public suspend fun getSet(setId: String): DomainResult<FlashcardSet>

    public suspend fun upsertSet(set: FlashcardSet): DomainResult<FlashcardSet>

    public suspend fun deleteSet(setId: String): DomainResult<Unit>

    public suspend fun upsertCard(card: Flashcard): DomainResult<Flashcard>

    public suspend fun deleteCard(id: String): DomainResult<Unit>

    /** Применить результат повторения. Атомарно обновляет SRS-поля карточки и dueCount набора. */
    public suspend fun submitReview(
        cardId: String,
        quality: ReviewQuality,
    ): DomainResult<Flashcard>

    public suspend fun refresh(ownerUserId: String): DomainResult<Unit>
}
