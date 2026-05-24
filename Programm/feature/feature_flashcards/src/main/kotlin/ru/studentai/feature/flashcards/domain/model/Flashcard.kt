package ru.studentai.feature.flashcards.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Карточка вопрос-ответ (ТЗ §4.2.9).
 *
 * Поля SRS (Spaced Repetition System):
 *  • [easiness]    — easiness-factor SM-2 (1.3..2.5+, обычно стартует с 2.5)
 *  • [intervalDays] — текущий интервал до следующего повторения, в днях
 *  • [repetitions]  — количество подряд успешных повторений
 *  • [nextReviewAt] — дата, когда карточка станет «due»; null если ещё ни разу не повторялась
 *  • [lastReviewedAt] — момент последнего просмотра (UTC)
 */
public data class Flashcard(
    public val id: String,
    public val setId: String,
    public val ownerUserId: String,
    public val front: String,
    public val back: String,
    public val easiness: Double = INITIAL_EASINESS,
    public val intervalDays: Int = 0,
    public val repetitions: Int = 0,
    public val nextReviewAt: LocalDate? = null,
    public val lastReviewedAt: Instant? = null,
    public val createdAt: Instant,
) {
    init {
        require(front.isNotBlank()) { "Flashcard.front must not be blank" }
        require(back.isNotBlank()) { "Flashcard.back must not be blank" }
        require(easiness >= MIN_EASINESS) {
            "easiness $easiness must be >= $MIN_EASINESS"
        }
        require(intervalDays >= 0) { "intervalDays must be >= 0" }
        require(repetitions >= 0) { "repetitions must be >= 0" }
    }

    public companion object {
        public const val INITIAL_EASINESS: Double = 2.5
        public const val MIN_EASINESS: Double = 1.3
    }
}
