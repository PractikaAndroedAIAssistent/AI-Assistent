package ru.studentai.feature.schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Учебный предмет (Room-сторона). PK — серверный/локальный UUID, не autoincrement.
 *
 * Уникальность по `(ownerUserId, name)` — у одного пользователя не может быть
 * двух предметов с тем же названием.
 */
@Entity(
    tableName = "subjects",
    indices = [
        Index(value = ["ownerUserId", "name"], unique = true),
    ],
)
public data class SubjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "ownerUserId")
    val ownerUserId: String,
    @ColumnInfo(name = "name")
    val name: String,
)
