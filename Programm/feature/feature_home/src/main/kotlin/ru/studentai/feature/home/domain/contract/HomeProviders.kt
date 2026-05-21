package ru.studentai.feature.home.domain.contract

import ru.studentai.feature.home.domain.model.AverageScoreSummary
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.GroupActivity
import ru.studentai.feature.home.domain.model.PendingReview
import ru.studentai.feature.home.domain.model.Recommendation
import ru.studentai.feature.home.domain.model.TeacherTask
import ru.studentai.feature.home.domain.model.UpcomingLesson

/**
 * Точка расширения «ближайшая пара». Реализуется в `feature_schedule`.
 * Возвращает `null`, если ближайших пар нет.
 */
public fun interface UpcomingLessonProvider {
    public suspend fun fetch(userId: String): UpcomingLesson?
}

/**
 * Точка расширения «дедлайны на неделю». Реализуется в `feature_tasks` (студент).
 *
 * Лимит передаётся явно вызывающим кодом — `fun interface` не поддерживает default values
 * на abstract-методе. Используйте [DEFAULT_LIMIT] как разумный «по умолчанию».
 */
public fun interface NearestDeadlinesProvider {
    public suspend fun fetch(userId: String, limit: Int): List<DeadlineItem>

    public companion object {
        public const val DEFAULT_LIMIT: Int = 5
    }
}

/**
 * Точка расширения «средний балл». Реализуется в `feature_grades`.
 */
public fun interface AverageScoreProvider {
    public suspend fun fetch(userId: String): AverageScoreSummary?
}

/**
 * Точка расширения «рекомендации по подготовке». Реализуется в `feature_grades`/`feature_ai`.
 */
public fun interface StudentRecommendationsProvider {
    public suspend fun fetch(userId: String, limit: Int): List<Recommendation>

    public companion object {
        public const val DEFAULT_LIMIT: Int = 3
    }
}

/**
 * Точка расширения «задачи преподавателя». Реализуется в `feature_tasks` (преподаватель).
 */
public fun interface TeacherTasksProvider {
    public suspend fun fetch(userId: String, limit: Int): List<TeacherTask>

    public companion object {
        public const val DEFAULT_LIMIT: Int = 5
    }
}

/**
 * Точка расширения «активность и успеваемость группы». Реализуется в `feature_grades`.
 */
public fun interface GroupActivityProvider {
    public suspend fun fetch(teacherId: String): GroupActivity?
}

/**
 * Точка расширения «работы на проверку». Реализуется в `feature_tasks`/`feature_grades`.
 */
public fun interface PendingReviewsProvider {
    public suspend fun fetch(teacherId: String, limit: Int): List<PendingReview>

    public companion object {
        public const val DEFAULT_LIMIT: Int = 5
    }
}
