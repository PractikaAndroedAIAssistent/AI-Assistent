package ru.studentai.feature.tasks.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TaskDto(
    @SerialName("id") val id: String,
    @SerialName("role") val role: String,
    @SerialName("subject_id") val subjectId: String? = null,
    @SerialName("subject_name") val subjectName: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("due_at") val dueAt: String,
    @SerialName("priority") val priority: String,
    @SerialName("is_completed") val isCompleted: Boolean,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("group_name") val groupName: String? = null,
)

@Serializable
public data class UpsertTaskRequest(
    @SerialName("role") val role: String,
    @SerialName("subject_id") val subjectId: String? = null,
    @SerialName("subject_name") val subjectName: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("due_at") val dueAt: String,
    @SerialName("priority") val priority: String,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("group_name") val groupName: String? = null,
)

@Serializable
public data class TaskListResponse(
    @SerialName("items") val items: List<TaskDto>,
)
