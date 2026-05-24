package ru.studentai.feature.flashcards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "flashcard_sets",
    indices = [Index(value = ["ownerUserId", "updatedAt"])],
)
public data class FlashcardSetEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "ownerUserId")
    val ownerUserId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "subjectName")
    val subjectName: String?,
    @ColumnInfo(name = "createdAt")
    val createdAt: Instant,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Instant,
)
