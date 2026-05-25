package ru.studentai.feature.tasks.domain.model

/**
 * Роль владельца задачи. Используется для дискриминации в Room и при запросах
 * (студент видит свои дедлайны, преподаватель — свои задачи).
 */
public enum class TaskRole {
    Student,
    Teacher,
}
