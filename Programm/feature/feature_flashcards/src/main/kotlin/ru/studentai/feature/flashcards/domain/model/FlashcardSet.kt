package ru.studentai.feature.flashcards.domain.model

import kotlinx.datetime.Instant

/**
 * Набор карточек (ТЗ §4.2.9). [cardCount] и [dueCount] денормализованы для быстрого
 * отображения списка наборов без дополнительных запросов.
 */
public data class FlashcardSet(
    public val id: String,
    public val ownerUserId: String,
    public val name: String,
    public val subjectName: String?,
    public val cardCount: Int,
    public val dueCount: Int,
    public val createdAt: Instant,
    public val updatedAt: Instant,
) {
    init {
        require(name.isNotBlank()) { "FlashcardSet.name must not be blank" }
        require(cardCount >= 0) { "cardCount must be >= 0" }
        require(dueCount in 0..cardCount) { "dueCount $dueCount out of [0, $cardCount]" }
    }
}
