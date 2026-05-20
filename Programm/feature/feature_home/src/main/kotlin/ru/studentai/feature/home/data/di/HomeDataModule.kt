package ru.studentai.feature.home.data.di

import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.feature.home.data.repository.HomeRepositoryImpl
import ru.studentai.feature.home.domain.contract.AverageScoreProvider
import ru.studentai.feature.home.domain.contract.GroupActivityProvider
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.contract.PendingReviewsProvider
import ru.studentai.feature.home.domain.contract.StudentRecommendationsProvider
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.home.domain.repository.HomeRepository

/**
 * Hilt-биндинги `feature_home`.
 *
 * Все провайдеры данных объявлены через [BindsOptionalOf] — это даёт `Optional<T>`
 * в [HomeRepositoryImpl] независимо от наличия реальных реализаций. Когда
 * соответствующая фича (например, feature_schedule) подключится и предоставит
 * `@Binds UpcomingLessonProvider`, Hilt автоматически заполнит Optional.
 *
 * Это позволяет:
 *  • разрабатывать feature_home в отрыве от feature_schedule / feature_tasks / feature_grades;
 *  • не править эту фичу при подключении новых фич-источников;
 *  • запускать приложение, имея только подключённую часть фич.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class HomeDataModule {

    @Binds
    @Singleton
    public abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @BindsOptionalOf
    public abstract fun optionalUpcomingLessonProvider(): UpcomingLessonProvider

    @BindsOptionalOf
    public abstract fun optionalNearestDeadlinesProvider(): NearestDeadlinesProvider

    @BindsOptionalOf
    public abstract fun optionalAverageScoreProvider(): AverageScoreProvider

    @BindsOptionalOf
    public abstract fun optionalStudentRecommendationsProvider(): StudentRecommendationsProvider

    @BindsOptionalOf
    public abstract fun optionalTeacherTasksProvider(): TeacherTasksProvider

    @BindsOptionalOf
    public abstract fun optionalGroupActivityProvider(): GroupActivityProvider

    @BindsOptionalOf
    public abstract fun optionalPendingReviewsProvider(): PendingReviewsProvider
}
