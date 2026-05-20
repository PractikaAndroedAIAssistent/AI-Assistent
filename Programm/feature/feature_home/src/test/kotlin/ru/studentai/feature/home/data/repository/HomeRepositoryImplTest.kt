package ru.studentai.feature.home.data.repository

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import java.util.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.home.domain.contract.AverageScoreProvider
import ru.studentai.feature.home.domain.contract.GroupActivityProvider
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.contract.PendingReviewsProvider
import ru.studentai.feature.home.domain.contract.StudentRecommendationsProvider
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.home.domain.model.AverageScoreSummary
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.DeadlinePriority
import ru.studentai.feature.home.domain.model.GroupActivity
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.model.PendingReview
import ru.studentai.feature.home.domain.model.Recommendation
import ru.studentai.feature.home.domain.model.ScoreTrend
import ru.studentai.feature.home.domain.model.TeacherTask
import ru.studentai.feature.home.domain.model.UpcomingLesson

class HomeRepositoryImplTest {

    private val dispatchers = object : DispatcherProvider {
        private val d = UnconfinedTestDispatcher()
        override val main: CoroutineDispatcher = d
        override val mainImmediate: CoroutineDispatcher = d
        override val io: CoroutineDispatcher = d
        override val default: CoroutineDispatcher = d
        override val unconfined: CoroutineDispatcher = d
    }

    private val student = User(
        id = "u1",
        email = "s@vuz.ru",
        fullName = "Иван Иванов",
        role = UserRole.Student,
    )

    private val teacher = User(
        id = "t1",
        email = "t@vuz.ru",
        fullName = "Петр Петров",
        role = UserRole.Teacher,
    )

    @Test
    fun `student snapshot with all providers empty returns Success with empty blocks`() = runTest {
        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.empty(),
            nearestDeadlinesProvider = Optional.empty(),
            averageScoreProvider = Optional.empty(),
            studentRecommendationsProvider = Optional.empty(),
            teacherTasksProvider = Optional.empty(),
            groupActivityProvider = Optional.empty(),
            pendingReviewsProvider = Optional.empty(),
            dispatchers = dispatchers,
        )

        val result = sut.loadStudentSnapshot(student)

        assertThat(result).isInstanceOf(DomainResult.Success::class)
        val snapshot = (result as DomainResult.Success).value
        assertThat(snapshot.upcomingLesson).isNull()
        assertThat(snapshot.weekDeadlines).isEmpty()
        assertThat(snapshot.averageScore).isNull()
        assertThat(snapshot.recommendations).isEmpty()
        assertThat(snapshot.isScheduleAvailable).isFalse()
        assertThat(snapshot.isDeadlinesAvailable).isFalse()
        assertThat(snapshot.isGradesAvailable).isFalse()
        assertThat(snapshot.isRecommendationsAvailable).isFalse()
    }

    @Test
    fun `student snapshot with providers returns combined data`() = runTest {
        val lesson = sampleLesson()
        val deadlines = listOf(sampleDeadline("d1"), sampleDeadline("d2"))
        val score = AverageScoreSummary(4.2, 5.0, ScoreTrend.Up, subjectCount = 6)
        val recs = listOf(Recommendation("r1", "Повторить", "Тема X"))

        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { _ -> lesson }),
            nearestDeadlinesProvider = Optional.of(NearestDeadlinesProvider { _, _ -> deadlines }),
            averageScoreProvider = Optional.of(AverageScoreProvider { _ -> score }),
            studentRecommendationsProvider = Optional.of(StudentRecommendationsProvider { _, _ -> recs }),
            teacherTasksProvider = Optional.empty(),
            groupActivityProvider = Optional.empty(),
            pendingReviewsProvider = Optional.empty(),
            dispatchers = dispatchers,
        )

        val snapshot = (sut.loadStudentSnapshot(student) as DomainResult.Success<HomeSnapshot.Student>).value

        assertThat(snapshot.upcomingLesson).isEqualTo(lesson)
        assertThat(snapshot.weekDeadlines).hasSize(2)
        assertThat(snapshot.averageScore).isEqualTo(score)
        assertThat(snapshot.recommendations).hasSize(1)
        assertThat(snapshot.isScheduleAvailable).isTrue()
        assertThat(snapshot.isDeadlinesAvailable).isTrue()
        assertThat(snapshot.isGradesAvailable).isTrue()
        assertThat(snapshot.isRecommendationsAvailable).isTrue()
    }

    @Test
    fun `provider throwing exception does not break the whole snapshot`() = runTest {
        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { _ -> throw RuntimeException("boom") }),
            nearestDeadlinesProvider = Optional.of(NearestDeadlinesProvider { _, _ -> listOf(sampleDeadline("d1")) }),
            averageScoreProvider = Optional.empty(),
            studentRecommendationsProvider = Optional.empty(),
            teacherTasksProvider = Optional.empty(),
            groupActivityProvider = Optional.empty(),
            pendingReviewsProvider = Optional.empty(),
            dispatchers = dispatchers,
        )

        val snapshot = (sut.loadStudentSnapshot(student) as DomainResult.Success<HomeSnapshot.Student>).value

        // Сломанный провайдер — лекция = null, но другие блоки заполнены
        assertThat(snapshot.upcomingLesson).isNull()
        assertThat(snapshot.weekDeadlines).hasSize(1)
        // А «доступность» остаётся true — UI отрисует EmptyState внутри блока
        assertThat(snapshot.isScheduleAvailable).isTrue()
    }

    @Test
    fun `teacher snapshot with providers returns combined data`() = runTest {
        val lesson = sampleLesson()
        val tasks = listOf(TeacherTask("t1", "Проверить лабы"))
        val activity = GroupActivity("Группа-101", studentCount = 24, averageScore = 4.1, maxScore = 5.0, submissionRatePercent = 72)
        val reviews = listOf(PendingReview("p1", "Группа-101", "Алгоритмы", pendingCount = 3, oldestSubmittedAt = sampleDateTime()))

        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { _ -> lesson }),
            nearestDeadlinesProvider = Optional.empty(),
            averageScoreProvider = Optional.empty(),
            studentRecommendationsProvider = Optional.empty(),
            teacherTasksProvider = Optional.of(TeacherTasksProvider { _, _ -> tasks }),
            groupActivityProvider = Optional.of(GroupActivityProvider { _ -> activity }),
            pendingReviewsProvider = Optional.of(PendingReviewsProvider { _, _ -> reviews }),
            dispatchers = dispatchers,
        )

        val snapshot = (sut.loadTeacherSnapshot(teacher) as DomainResult.Success<HomeSnapshot.Teacher>).value

        assertThat(snapshot.upcomingLesson).isNotNull()
        assertThat(snapshot.tasks).hasSize(1)
        assertThat(snapshot.groupActivity).isEqualTo(activity)
        assertThat(snapshot.pendingReviews).hasSize(1)
    }

    private fun sampleDateTime(): LocalDateTime = LocalDateTime.parse("2026-05-18T10:00:00")

    private fun sampleLesson() = UpcomingLesson(
        startAt = LocalDateTime.parse("2026-05-18T10:00:00"),
        endAt = LocalDateTime.parse("2026-05-18T11:30:00"),
        subject = "Алгоритмы",
        lessonType = "Лекция",
        room = "305",
        teacher = "Петров П.П.",
    )

    private fun sampleDeadline(id: String) = DeadlineItem(
        id = id,
        subject = "Алгоритмы",
        title = "Сдать ЛР №2",
        dueAt = LocalDateTime.parse("2026-05-22T23:59:00"),
        priority = DeadlinePriority.Normal,
        isOverdue = false,
    )
}
