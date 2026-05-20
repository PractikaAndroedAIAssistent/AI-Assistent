package ru.studentai.feature.home.domain.model

import androidx.compose.runtime.Immutable
import ru.studentai.feature.auth.domain.model.User

/**
 * Снимок данных главного экрана. Дискриминируется по роли пользователя.
 *
 * Поля содержат `null`, если соответствующий [Provider] не предоставлен в графе
 * (например, feature_schedule ещё не подключена) — UI рендерит EmptyState вместо
 * ошибки или пустых данных.
 */
@Immutable
public sealed interface HomeSnapshot {

    public val user: User

    @Immutable
    public data class Student(
        override val user: User,
        public val upcomingLesson: UpcomingLesson? = null,
        public val weekDeadlines: List<DeadlineItem> = emptyList(),
        public val averageScore: AverageScoreSummary? = null,
        public val recommendations: List<Recommendation> = emptyList(),
        public val isScheduleAvailable: Boolean = false,
        public val isDeadlinesAvailable: Boolean = false,
        public val isGradesAvailable: Boolean = false,
        public val isRecommendationsAvailable: Boolean = false,
    ) : HomeSnapshot

    @Immutable
    public data class Teacher(
        override val user: User,
        public val upcomingLesson: UpcomingLesson? = null,
        public val tasks: List<TeacherTask> = emptyList(),
        public val groupActivity: GroupActivity? = null,
        public val pendingReviews: List<PendingReview> = emptyList(),
        public val isScheduleAvailable: Boolean = false,
        public val isTasksAvailable: Boolean = false,
        public val isGroupActivityAvailable: Boolean = false,
        public val isPendingReviewsAvailable: Boolean = false,
    ) : HomeSnapshot
}
