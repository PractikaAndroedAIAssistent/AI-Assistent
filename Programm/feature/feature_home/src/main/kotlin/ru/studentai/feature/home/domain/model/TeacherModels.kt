package ru.studentai.feature.home.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Задача преподавателя (ТЗ §4.2.4: задачи проверки, подготовка материалов).
 *
 * @param id          стабильный ключ
 * @param title       краткое описание задачи
 * @param dueAt       срок (если есть)
 * @param relatedSubject связанный предмет (опционально)
 * @param isOverdue   просроченная задача
 */
public data class TeacherTask(
    public val id: String,
    public val title: String,
    public val dueAt: LocalDateTime? = null,
    public val relatedSubject: String? = null,
    public val isOverdue: Boolean = false,
)

/**
 * Сводка активности и успеваемости группы для преподавателя (ТЗ §4.2.10).
 *
 * Без личных данных студентов — только агрегированные показатели.
 *
 * @param groupName            название группы
 * @param studentCount         количество студентов
 * @param averageScore         средний балл группы
 * @param maxScore             масштаб шкалы
 * @param submissionRatePercent доля сданных работ за период (%)
 */
public data class GroupActivity(
    public val groupName: String,
    public val studentCount: Int,
    public val averageScore: Double,
    public val maxScore: Double,
    public val submissionRatePercent: Int,
) {
    init {
        require(studentCount >= 0) { "studentCount must be >= 0" }
        require(averageScore in 0.0..maxScore) { "averageScore $averageScore out of [0, $maxScore]" }
        require(maxScore > 0.0) { "maxScore must be > 0" }
        require(submissionRatePercent in 0..100) {
            "submissionRatePercent must be in 0..100, got $submissionRatePercent"
        }
    }
}

/**
 * Напоминание о проверке работ (ТЗ §4.2.2 для преподавателя).
 *
 * @param id              стабильный ключ
 * @param groupName       группа, для которой ожидается проверка
 * @param subject         предмет
 * @param pendingCount    число работ, ожидающих проверки
 * @param oldestSubmittedAt дата самой старой непросмотренной работы (для приоритизации)
 */
public data class PendingReview(
    public val id: String,
    public val groupName: String,
    public val subject: String,
    public val pendingCount: Int,
    public val oldestSubmittedAt: LocalDateTime,
) {
    init {
        require(pendingCount > 0) { "pendingCount must be > 0 (use empty list otherwise)" }
    }
}
