package ru.studentai.feature.flashcards.domain.algorithm

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Test
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.ReviewQuality

class Sm2AlgorithmTest {

    private val sut = Sm2Algorithm()
    private val fixedNow: Instant = Instant.parse("2026-05-20T10:00:00Z")
    private val zone: TimeZone = TimeZone.UTC

    @Test
    fun `Unknown quality resets repetitions and sets interval to 1 day`() {
        val card = newCard()
        val updated = sut.apply(card, ReviewQuality.Unknown, now = fixedNow, zone = zone)
        assertThat(updated.repetitions).isEqualTo(0)
        assertThat(updated.intervalDays).isEqualTo(1)
        assertThat(updated.nextReviewAt).isEqualTo(LocalDate(2026, 5, 21))
    }

    @Test
    fun `Bad quality resets repetitions and sets interval to 1 day`() {
        val card = newCard().copy(repetitions = 3, intervalDays = 10)
        val updated = sut.apply(card, ReviewQuality.Bad, now = fixedNow, zone = zone)
        assertThat(updated.repetitions).isEqualTo(0)
        assertThat(updated.intervalDays).isEqualTo(1)
    }

    @Test
    fun `first successful repetition sets interval to 1 day`() {
        val card = newCard()
        val updated = sut.apply(card, ReviewQuality.Normal, now = fixedNow, zone = zone)
        assertThat(updated.repetitions).isEqualTo(1)
        assertThat(updated.intervalDays).isEqualTo(1)
    }

    @Test
    fun `second successful repetition sets interval to 6 days`() {
        val card = newCard().copy(repetitions = 1, intervalDays = 1)
        val updated = sut.apply(card, ReviewQuality.Good, now = fixedNow, zone = zone)
        assertThat(updated.repetitions).isEqualTo(2)
        assertThat(updated.intervalDays).isEqualTo(6)
    }

    @Test
    fun `third repetition uses easiness multiplier`() {
        val card = newCard().copy(repetitions = 2, intervalDays = 6, easiness = 2.5)
        val updated = sut.apply(card, ReviewQuality.Good, now = fixedNow, zone = zone)
        assertThat(updated.repetitions).isEqualTo(3)
        // ceil(6 * 2.5) = 15
        assertThat(updated.intervalDays).isEqualTo(15)
    }

    @Test
    fun `Excellent quality increases easiness factor`() {
        val card = newCard()
        val updated = sut.apply(card, ReviewQuality.Excellent, now = fixedNow, zone = zone)
        assertThat(updated.easiness).isGreaterThan(card.easiness)
    }

    @Test
    fun `Bad quality decreases easiness but not below floor`() {
        val card = newCard().copy(easiness = 1.4)
        val updated = sut.apply(card, ReviewQuality.Bad, now = fixedNow, zone = zone)
        assertThat(updated.easiness).isCloseTo(Flashcard.MIN_EASINESS, 0.0001)
    }

    @Test
    fun `pickDueCards returns null-nextReviewAt cards first`() {
        val a = newCard().copy(id = "a", nextReviewAt = null)
        val b = newCard().copy(id = "b", nextReviewAt = LocalDate(2026, 5, 25))
        val due = sut.pickDueCards(listOf(b, a), now = fixedNow, zone = zone)
        assertThat(due).isEqualTo(listOf(a)) // только a — у b nextReviewAt в будущем
    }

    @Test
    fun `pickDueCards includes today and past dates`() {
        val today = newCard().copy(id = "t", nextReviewAt = LocalDate(2026, 5, 20))
        val past = newCard().copy(id = "p", nextReviewAt = LocalDate(2026, 5, 18))
        val future = newCard().copy(id = "f", nextReviewAt = LocalDate(2026, 5, 22))
        val due = sut.pickDueCards(listOf(today, past, future), now = fixedNow, zone = zone)
        assertThat(due.map { it.id }).isEqualTo(listOf("p", "t"))
    }

    @Test
    fun `apply persists lastReviewedAt`() {
        val card = newCard()
        val updated = sut.apply(card, ReviewQuality.Normal, now = fixedNow, zone = zone)
        assertThat(updated.lastReviewedAt).isNotNull()
        assertThat(updated.lastReviewedAt).isEqualTo(fixedNow)
    }

    private fun newCard(): Flashcard = Flashcard(
        id = "c1",
        setId = "s1",
        ownerUserId = "u1",
        front = "Q",
        back = "A",
        createdAt = Clock.System.now(),
    )
}
