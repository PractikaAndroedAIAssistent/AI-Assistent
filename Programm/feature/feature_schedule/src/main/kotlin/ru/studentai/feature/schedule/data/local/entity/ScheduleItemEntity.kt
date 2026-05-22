package ru.studentai.feature.schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

/**
 * Занятие в расписании (Room).
 *
 * Денормализуем `subjectName` сюда — это позволяет:
 *  • избежать JOIN в hot-path запросах списков расписания;
 *  • показывать корректное имя, даже если subject будет удалён (FK SET_NULL).
 *
 * Индексы:
 *  • `(ownerUserId, startAt)` — основной для запросов «занятия за день/неделю»;
 *  • `subjectId` — FK + фильтр по предмету.
 */
@Entity(
    tableName = "schedule_items",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["ownerUserId", "startAt"]),
        Index(value = ["subjectId"]),
    ],
)
public data class ScheduleItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "ownerUserId")
    val ownerUserId: String,
    @ColumnInfo(name = "subjectId")
    val subjectId: String?,
    @ColumnInfo(name = "subjectName")
    val subjectName: String,
    @ColumnInfo(name = "lessonType")
    val lessonType: String,
    @ColumnInfo(name = "customTypeLabel")
    val customTypeLabel: String?,
    @ColumnInfo(name = "startAt")
    val startAt: LocalDateTime,
    @ColumnInfo(name = "endAt")
    val endAt: LocalDateTime,
    @ColumnInfo(name = "room")
    val room: String?,
    @ColumnInfo(name = "teacher")
    val teacher: String?,
    @ColumnInfo(name = "note")
    val note: String?,
)
