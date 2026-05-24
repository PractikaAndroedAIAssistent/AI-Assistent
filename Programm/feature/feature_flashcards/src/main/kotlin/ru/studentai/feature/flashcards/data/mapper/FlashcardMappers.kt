package ru.studentai.feature.flashcards.data.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import ru.studentai.feature.flashcards.data.local.dao.FlashcardSetWithCounts
import ru.studentai.feature.flashcards.data.local.entity.FlashcardEntity
import ru.studentai.feature.flashcards.data.local.entity.FlashcardSetEntity
import ru.studentai.feature.flashcards.data.remote.dto.FlashcardDto
import ru.studentai.feature.flashcards.data.remote.dto.FlashcardSetDto
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.FlashcardSet

// ─── Entities ↔ Domain ────────────────────────────────────────────────────────

internal fun FlashcardSetWithCounts.toDomain(): FlashcardSet = FlashcardSet(
    id = set.id,
    ownerUserId = set.ownerUserId,
    name = set.name,
    subjectName = set.subjectName,
    cardCount = cardCount,
    dueCount = dueCount,
    createdAt = set.createdAt,
    updatedAt = set.updatedAt,
)

internal fun FlashcardSet.toEntity(): FlashcardSetEntity = FlashcardSetEntity(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    subjectName = subjectName,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun Flashcard.toEntity(): FlashcardEntity = FlashcardEntity(
    id = id,
    setId = setId,
    ownerUserId = ownerUserId,
    front = front,
    back = back,
    easiness = easiness,
    intervalDays = intervalDays,
    repetitions = repetitions,
    nextReviewAt = nextReviewAt,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt,
)

internal fun FlashcardEntity.toDomain(): Flashcard = Flashcard(
    id = id,
    setId = setId,
    ownerUserId = ownerUserId,
    front = front,
    back = back,
    easiness = easiness,
    intervalDays = intervalDays,
    repetitions = repetitions,
    nextReviewAt = nextReviewAt,
    lastReviewedAt = lastReviewedAt,
    createdAt = createdAt,
)

// ─── DTO ↔ Domain ─────────────────────────────────────────────────────────────

internal fun FlashcardSetDto.toDomain(ownerUserId: String): FlashcardSet = FlashcardSet(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    subjectName = subjectName,
    cardCount = 0,
    dueCount = 0,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

internal fun FlashcardDto.toDomain(ownerUserId: String): Flashcard = Flashcard(
    id = id,
    setId = setId,
    ownerUserId = ownerUserId,
    front = front,
    back = back,
    easiness = easiness,
    intervalDays = intervalDays,
    repetitions = repetitions,
    nextReviewAt = nextReviewAt?.let { LocalDate.parse(it) },
    lastReviewedAt = lastReviewedAt?.let { Instant.parse(it) },
    createdAt = Instant.parse(createdAt),
)
