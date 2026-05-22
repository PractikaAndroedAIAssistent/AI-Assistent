package ru.studentai.feature.schedule.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ScheduleItemDto(
    @SerialName("id") val id: String,
    @SerialName("subject_id") val subjectId: String?,
    @SerialName("subject_name") val subjectName: String,
    @SerialName("lesson_type") val lessonType: String,
    @SerialName("custom_type_label") val customTypeLabel: String? = null,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("room") val room: String? = null,
    @SerialName("teacher") val teacher: String? = null,
    @SerialName("note") val note: String? = null,
)

@Serializable
public data class SubjectDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)

@Serializable
public data class ScheduleSyncResponse(
    @SerialName("subjects") val subjects: List<SubjectDto>,
    @SerialName("items") val items: List<ScheduleItemDto>,
)

@Serializable
public data class ImportResponse(
    @SerialName("imported_count") val importedCount: Int,
)

@Serializable
public data class UpsertScheduleItemRequest(
    @SerialName("subject_id") val subjectId: String?,
    @SerialName("subject_name") val subjectName: String,
    @SerialName("lesson_type") val lessonType: String,
    @SerialName("custom_type_label") val customTypeLabel: String? = null,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("room") val room: String? = null,
    @SerialName("teacher") val teacher: String? = null,
    @SerialName("note") val note: String? = null,
)
