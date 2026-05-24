package ru.studentai.feature.flashcards.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.FlashcardSet
import ru.studentai.feature.flashcards.domain.model.ReviewQuality
import ru.studentai.feature.flashcards.domain.repository.FlashcardRepository

public class ObserveSetsUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public operator fun invoke(ownerUserId: String): Flow<List<FlashcardSet>> =
        repository.observeSets(ownerUserId)
}

public class ObserveCardsInSetUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public operator fun invoke(setId: String): Flow<List<Flashcard>> =
        repository.observeCardsInSet(setId)
}

public class GetSetUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(setId: String): DomainResult<FlashcardSet> =
        repository.getSet(setId)
}

public class UpsertSetUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(set: FlashcardSet): DomainResult<FlashcardSet> =
        repository.upsertSet(set)
}

public class DeleteSetUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(setId: String): DomainResult<Unit> =
        repository.deleteSet(setId)
}

public class UpsertCardUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(card: Flashcard): DomainResult<Flashcard> =
        repository.upsertCard(card)
}

public class DeleteCardUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<Unit> =
        repository.deleteCard(id)
}

public class SubmitReviewUseCase @Inject constructor(
    private val repository: FlashcardRepository,
) {
    public suspend operator fun invoke(
        cardId: String,
        quality: ReviewQuality,
    ): DomainResult<Flashcard> = repository.submitReview(cardId, quality)
}
