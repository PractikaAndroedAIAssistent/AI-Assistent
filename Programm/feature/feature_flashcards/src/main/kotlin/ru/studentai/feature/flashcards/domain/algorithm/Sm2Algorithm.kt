package ru.studentai.feature.flashcards.domain.algorithm

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.ReviewQuality

/**
 * Реализация алгоритма SuperMemo SM-2 (ТЗ §4.2.9: «алгоритм интервального повторения»).
 *
 * Правила (формулы согласно оригинальной публикации Wozniak'а, адаптированные под шкалу 0..4):
 *  • Если quality.score < 2 (Unknown / Bad) — repetitions := 0; intervalDays := 1.
 *  • Иначе:
 *      - repetitions += 1;
 *      - intervalDays:
 *          repetitions == 1 → 1
 *          repetitions == 2 → 6
 *          repetitions > 2  → ceil(prevInterval * easiness)
 *      - easiness обновляется по формуле:
 *          EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
 *        где `q` пересчитан к шкале 0..5: `q = score + 1` (наш 4 ↔ оригинальный 5).
 *        EF не опускается ниже [Flashcard.MIN_EASINESS] = 1.3.
 *  • nextReviewAt = today + intervalDays
 */
@Singleton
public class Sm2Algorithm @Inject constructor() {

    /** Применяет результат повторения к карточке и возвращает обновлённую. */
    public fun apply(
        card: Flashcard,
        quality: ReviewQuality,
        now: Instant = Clock.System.now(),
        zone: TimeZone = TimeZone.currentSystemDefault(),
    ): Flashcard {
        val today = now.toLocalDateTime(zone).date
        return if (!quality.isRemembered) {
            card.copy(
                repetitions = 0,
                intervalDays = 1,
                easiness = updateEasiness(card.easiness, quality),
                nextReviewAt = today.plus(DatePeriod(days = 1)),
                lastReviewedAt = now,
            )
        } else {
            val nextReps = card.repetitions + 1
            val nextInterval = nextInterval(
                repetitions = nextReps,
                previousInterval = card.intervalDays,
                easiness = card.easiness,
            )
            card.copy(
                repetitions = nextReps,
                intervalDays = nextInterval,
                easiness = updateEasiness(card.easiness, quality),
                nextReviewAt = today.plus(DatePeriod(days = nextInterval)),
                lastReviewedAt = now,
            )
        }
    }

    /** Возвращает список карточек, готовых к повторению сегодня (due) — отсортированный. */
    public fun pickDueCards(
        all: List<Flashcard>,
        now: Instant = Clock.System.now(),
        zone: TimeZone = TimeZone.currentSystemDefault(),
    ): List<Flashcard> {
        val today: LocalDate = now.toLocalDateTime(zone).date
        return all
            .filter { card -> card.nextReviewAt == null || card.nextReviewAt <= today }
            .sortedWith(compareBy<Flashcard> { it.nextReviewAt }.thenBy { it.createdAt })
    }

    private fun updateEasiness(current: Double, quality: ReviewQuality): Double {
        val q = (quality.score + 1).toDouble() // 0..4 → 1..5
        val newValue = current + (0.1 - (5.0 - q) * (0.08 + (5.0 - q) * 0.02))
        return newValue.coerceAtLeast(Flashcard.MIN_EASINESS)
    }

    private fun nextInterval(repetitions: Int, previousInterval: Int, easiness: Double): Int = when {
        repetitions == 1 -> 1
        repetitions == 2 -> 6
        else -> kotlin.math.ceil(previousInterval.coerceAtLeast(1) * easiness).toInt()
    }
}
