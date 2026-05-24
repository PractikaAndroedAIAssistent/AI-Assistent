package ru.studentai.feature.tasks.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * Учебная задача (Room). Объединяет дедлайн студента и задачу преподавателя
 * через дискриминатор [role].
 *
 * Преподавательский вариант обязан иметь непустой `groupName`; для студента поле
 * остаётся `null`.
 *
 * Индексы:
 *  • `(ownerUserId, role, dueAt)` — основной для запросов «активные задачи»;
 *  • `(ownerUserId, role, isCompleted)` — фильтр по статусу.
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["ownerUserId", "role", "dueAt"]),
        Index(value = ["ownerUserId", "role", "isCompleted"]),
    ],
)
public data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "ownerUserId")
    val ownerUserId: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "subjectId")
    val subjectId: String?,
    @ColumnInfo(name = "subjectName")
    val subjectName: String?,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "dueAt")
    val dueAt: LocalDateTime,
    @ColumnInfo(name = "priority")
    val priority: String,
    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean,
    @ColumnInfo(name = "completedAt")
    val completedAt: LocalDateTime?,
    @ColumnInfo(name = "groupName")
    val groupName: String?,
)
