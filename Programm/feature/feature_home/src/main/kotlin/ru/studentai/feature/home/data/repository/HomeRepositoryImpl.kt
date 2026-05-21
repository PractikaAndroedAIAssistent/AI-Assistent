package ru.studentai.feature.home.data.repository

import java.util.Optional
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCall
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.home.domain.contract.AverageScoreProvider
import ru.studentai.feature.home.domain.contract.GroupActivityProvider
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.contract.PendingReviewsProvider
import ru.studentai.feature.home.domain.contract.StudentRecommendationsProvider
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.repository.HomeRepository

/**
 * Реализация [HomeRepository].
 *
 * Каждая зависимость провайдера — `Optional<T>` через Hilt `@BindsOptionalOf`.
 * Если фича, реализующая провайдер (например, feature_schedule), ещё не подключена,
 * Optional.empty() — соответствующий блок остаётся пустым, но snapshot собирается.
 *
 * Все вызовы провайдеров выполняются **параллельно** через `async { ... }.awaitAll()`
 * — главный экран не ждёт суммы латенций всех источников.
 *
 * Каждый блок защищён собственным [safeCall] — ошибка одного провайдера НЕ ломает
 * весь экран; на месте этого блока показывается EmptyState («Раздел недоступен»).
 */
@Singleton
public class HomeRepositoryImpl @Inject constructor(
    private val upcomingLessonProvider: Optional<UpcomingLessonProvider>,
    private val nearestDeadlinesProvider: Optional<NearestDeadlinesProvider>,
    private val averageScoreProvider: Optional<AverageScoreProvider>,
    private val studentRecommendationsProvider: Optional<StudentRecommendationsProvider>,
    private val teacherTasksProvider: Optional<TeacherTasksProvider>,
    private val groupActivityProvider: Optional<GroupActivityProvider>,
    private val pendingReviewsProvider: Optional<PendingReviewsProvider>,
    private val dispatchers: DispatcherProvider,
) : HomeRepository {

    override suspend fun loadStudentSnapshot(user: User): DomainResult<HomeSnapshot.Student> =
        safeCall {
            coroutineScope {
                val lessonDeferred = async(dispatchers.io) {
                    upcomingLessonProvider.fetchOrNull { it.fetch(user.id) }
                }
                val deadlinesDeferred = async(dispatchers.io) {
                    nearestDeadlinesProvider.fetchOrEmpty {
                        it.fetch(user.id, NearestDeadlinesProvider.DEFAULT_LIMIT)
                    }
                }
                val scoreDeferred = async(dispatchers.io) {
                    averageScoreProvider.fetchOrNull { it.fetch(user.id) }
                }
                val recsDeferred = async(dispatchers.io) {
                    studentRecommendationsProvider.fetchOrEmpty {
                        it.fetch(user.id, StudentRecommendationsProvider.DEFAULT_LIMIT)
                    }
                }
                awaitAll(lessonDeferred, deadlinesDeferred, scoreDeferred, recsDeferred)

                HomeSnapshot.Student(
                    user = user,
                    upcomingLesson = lessonDeferred.await(),
                    weekDeadlines = deadlinesDeferred.await(),
                    averageScore = scoreDeferred.await(),
                    recommendations = recsDeferred.await(),
                    isScheduleAvailable = upcomingLessonProvider.isPresent,
                    isDeadlinesAvailable = nearestDeadlinesProvider.isPresent,
                    isGradesAvailable = averageScoreProvider.isPresent,
                    isRecommendationsAvailable = studentRecommendationsProvider.isPresent,
                )
            }
        }

    override suspend fun loadTeacherSnapshot(user: User): DomainResult<HomeSnapshot.Teacher> =
        safeCall {
            coroutineScope {
                val lessonDeferred = async(dispatchers.io) {
                    upcomingLessonProvider.fetchOrNull { it.fetch(user.id) }
                }
                val tasksDeferred = async(dispatchers.io) {
                    teacherTasksProvider.fetchOrEmpty {
                        it.fetch(user.id, TeacherTasksProvider.DEFAULT_LIMIT)
                    }
                }
                val activityDeferred = async(dispatchers.io) {
                    groupActivityProvider.fetchOrNull { it.fetch(user.id) }
                }
                val reviewsDeferred = async(dispatchers.io) {
                    pendingReviewsProvider.fetchOrEmpty {
                        it.fetch(user.id, PendingReviewsProvider.DEFAULT_LIMIT)
                    }
                }
                awaitAll(lessonDeferred, tasksDeferred, activityDeferred, reviewsDeferred)

                HomeSnapshot.Teacher(
                    user = user,
                    upcomingLesson = lessonDeferred.await(),
                    tasks = tasksDeferred.await(),
                    groupActivity = activityDeferred.await(),
                    pendingReviews = reviewsDeferred.await(),
                    isScheduleAvailable = upcomingLessonProvider.isPresent,
                    isTasksAvailable = teacherTasksProvider.isPresent,
                    isGroupActivityAvailable = groupActivityProvider.isPresent,
                    isPendingReviewsAvailable = pendingReviewsProvider.isPresent,
                )
            }
        }

    /**
     * Безопасный вызов провайдера: ошибка одного источника не валит весь экран.
     * Возвращает `null` если Optional пуст ИЛИ если вызов бросил исключение.
     */
    private suspend inline fun <P : Any, R> Optional<P>.fetchOrNull(
        crossinline call: suspend (P) -> R?,
    ): R? {
        val provider = orElseNull() ?: return null
        return when (val result = safeCall { call(provider) }) {
            is DomainResult.Success -> result.value
            is DomainResult.Failure -> null
        }
    }

    private suspend inline fun <P : Any, R> Optional<P>.fetchOrEmpty(
        crossinline call: suspend (P) -> List<R>,
    ): List<R> {
        val provider = orElseNull() ?: return emptyList()
        return when (val result = safeCall { call(provider) }) {
            is DomainResult.Success -> result.value
            is DomainResult.Failure -> emptyList()
        }
    }

    private fun <T> Optional<T>.orElseNull(): T? = if (isPresent) get() else null
}
