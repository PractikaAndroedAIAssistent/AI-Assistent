package ru.studentai.tests.home

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isTrue
import java.util.Optional
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.home.data.repository.HomeRepositoryImpl
import ru.studentai.feature.home.domain.contract.AverageScoreProvider
import ru.studentai.feature.home.domain.contract.GroupActivityProvider
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.contract.PendingReviewsProvider
import ru.studentai.feature.home.domain.contract.StudentRecommendationsProvider
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.tests.home.support.HomeFixtures
import ru.studentai.tests.home.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class HomeRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestDispatcherProvider(dispatcher)

    @Test
    fun `student snapshot passes default limits to providers`() = runTest(dispatcher) {
        var deadlinesLimit: Int? = null
        var recommendationsLimit: Int? = null
        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { HomeFixtures.upcomingLesson() }),
            nearestDeadlinesProvider = Optional.of(
                NearestDeadlinesProvider { _, limit ->
                    deadlinesLimit = limit
                    listOf(HomeFixtures.deadline())
                },
            ),
            averageScoreProvider = Optional.of(AverageScoreProvider { HomeFixtures.averageScore() }),
            studentRecommendationsProvider = Optional.of(
                StudentRecommendationsProvider { _, limit ->
                    recommendationsLimit = limit
                    listOf(HomeFixtures.recommendation())
                },
            ),
            teacherTasksProvider = Optional.empty(),
            groupActivityProvider = Optional.empty(),
            pendingReviewsProvider = Optional.empty(),
            dispatchers = dispatchers,
        )

        val result = sut.loadStudentSnapshot(HomeFixtures.studentUser())

        assertThat(result).isInstanceOf(DomainResult.Success::class)
        assertThat(deadlinesLimit).isEqualTo(NearestDeadlinesProvider.DEFAULT_LIMIT)
        assertThat(recommendationsLimit).isEqualTo(StudentRecommendationsProvider.DEFAULT_LIMIT)
    }

    @Test
    fun `teacher snapshot with empty providers returns empty blocks and unavailable flags`() = runTest(dispatcher) {
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

        val result = sut.loadTeacherSnapshot(HomeFixtures.teacherUser())

        assertThat(result).isInstanceOf(DomainResult.Success::class)
        val snapshot = (result as DomainResult.Success<HomeSnapshot.Teacher>).value
        assertThat(snapshot.upcomingLesson).isNull()
        assertThat(snapshot.tasks).isEmpty()
        assertThat(snapshot.groupActivity).isNull()
        assertThat(snapshot.pendingReviews).isEmpty()
        assertThat(snapshot.isScheduleAvailable).isFalse()
        assertThat(snapshot.isTasksAvailable).isFalse()
        assertThat(snapshot.isGroupActivityAvailable).isFalse()
        assertThat(snapshot.isPendingReviewsAvailable).isFalse()
    }

    @Test
    fun `teacher snapshot passes default limits to tasks and reviews providers`() = runTest(dispatcher) {
        var tasksLimit: Int? = null
        var reviewsLimit: Int? = null
        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { HomeFixtures.upcomingLesson() }),
            nearestDeadlinesProvider = Optional.empty(),
            averageScoreProvider = Optional.empty(),
            studentRecommendationsProvider = Optional.empty(),
            teacherTasksProvider = Optional.of(
                TeacherTasksProvider { _, limit ->
                    tasksLimit = limit
                    listOf(HomeFixtures.teacherTask())
                },
            ),
            groupActivityProvider = Optional.of(GroupActivityProvider { HomeFixtures.groupActivity() }),
            pendingReviewsProvider = Optional.of(
                PendingReviewsProvider { _, limit ->
                    reviewsLimit = limit
                    listOf(HomeFixtures.pendingReview())
                },
            ),
            dispatchers = dispatchers,
        )

        val result = sut.loadTeacherSnapshot(HomeFixtures.teacherUser())

        assertThat(result).isInstanceOf(DomainResult.Success::class)
        assertThat(tasksLimit).isEqualTo(TeacherTasksProvider.DEFAULT_LIMIT)
        assertThat(reviewsLimit).isEqualTo(PendingReviewsProvider.DEFAULT_LIMIT)
    }

    @Test
    fun `teacher provider failure does not break snapshot`() = runTest(dispatcher) {
        val sut = HomeRepositoryImpl(
            upcomingLessonProvider = Optional.of(UpcomingLessonProvider { HomeFixtures.upcomingLesson() }),
            nearestDeadlinesProvider = Optional.empty(),
            averageScoreProvider = Optional.empty(),
            studentRecommendationsProvider = Optional.empty(),
            teacherTasksProvider = Optional.of(TeacherTasksProvider { _, _ -> throw RuntimeException("boom") }),
            groupActivityProvider = Optional.of(GroupActivityProvider { HomeFixtures.groupActivity() }),
            pendingReviewsProvider = Optional.empty(),
            dispatchers = dispatchers,
        )

        val snapshot = (sut.loadTeacherSnapshot(HomeFixtures.teacherUser()) as DomainResult.Success<HomeSnapshot.Teacher>).value

        assertThat(snapshot.tasks).isEmpty()
        assertThat(snapshot.groupActivity).isEqualTo(HomeFixtures.groupActivity())
        assertThat(snapshot.isTasksAvailable).isTrue()
    }
}
