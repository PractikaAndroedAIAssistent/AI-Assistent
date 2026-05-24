package ru.studentai.feature.flashcards.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class FlashcardSetDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("subject_name") val subjectName: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
public data class FlashcardDto(
    @SerialName("id") val id: String,
    @SerialName("set_id") val setId: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("easiness") val easiness: Double,
    @SerialName("interval_days") val intervalDays: Int,
    @SerialName("repetitions") val repetitions: Int,
    @SerialName("next_review_at") val nextReviewAt: String? = null,
    @SerialName("last_reviewed_at") val lastReviewedAt: String? = null,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
public data class FlashcardSyncResponse(
    @SerialName("sets") val sets: List<FlashcardSetDto>,
    @SerialName("cards") val cards: List<FlashcardDto>,
)

@Serializable
public data class UpsertSetRequest(
    @SerialName("name") val name: String,
    @SerialName("subject_name") val subjectName: String? = null,
)

@Serializable
public data class UpsertCardRequest(
    @SerialName("set_id") val setId: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
)

@Serializable
public data class ReviewSubmitRequest(
    @SerialName("quality_score") val qualityScore: Int,
)
