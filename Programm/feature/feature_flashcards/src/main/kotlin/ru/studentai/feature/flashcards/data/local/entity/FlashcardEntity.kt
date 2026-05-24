package ru.studentai.feature.flashcards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = FlashcardSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["setId", "nextReviewAt"]),
        Index(value = ["ownerUserId"]),
    ],
)
public data class FlashcardEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "setId")
    val setId: String,
    @ColumnInfo(name = "ownerUserId")
    val ownerUserId: String,
    @ColumnInfo(name = "front")
    val front: String,
    @ColumnInfo(name = "back")
    val back: String,
    @ColumnInfo(name = "easiness")
    val easiness: Double,
    @ColumnInfo(name = "intervalDays")
    val intervalDays: Int,
    @ColumnInfo(name = "repetitions")
    val repetitions: Int,
    @ColumnInfo(name = "nextReviewAt")
    val nextReviewAt: LocalDate?,
    @ColumnInfo(name = "lastReviewedAt")
    val lastReviewedAt: Instant?,
    @ColumnInfo(name = "createdAt")
    val createdAt: Instant,
)
