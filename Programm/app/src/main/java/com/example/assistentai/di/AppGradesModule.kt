package com.example.assistentai.di

import com.example.assistentai.grades.DemoGradeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.feature.grades.domain.repository.GradeRepository
import ru.studentai.feature.grades.integration.home.GradesAverageScoreProvider
import ru.studentai.feature.grades.integration.home.GradesGroupActivityProvider
import ru.studentai.feature.grades.integration.home.GradesRecommendationsProvider
import ru.studentai.feature.home.domain.contract.AverageScoreProvider
import ru.studentai.feature.home.domain.contract.GroupActivityProvider
import ru.studentai.feature.home.domain.contract.StudentRecommendationsProvider

/**
 * App-уровневые биндинги оценок:
 *  • [GradeRepository] → [DemoGradeRepository]
 *  • 3 plugin-points feature_home: AverageScoreProvider, StudentRecommendationsProvider,
 *    GroupActivityProvider
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class AppGradesModule {

    @Binds
    @Singleton
    public abstract fun bindGradeRepository(impl: DemoGradeRepository): GradeRepository

    @Binds
    @Singleton
    public abstract fun bindAverageScoreProvider(
        impl: GradesAverageScoreProvider,
    ): AverageScoreProvider

    @Binds
    @Singleton
    public abstract fun bindStudentRecommendationsProvider(
        impl: GradesRecommendationsProvider,
    ): StudentRecommendationsProvider

    @Binds
    @Singleton
    public abstract fun bindGroupActivityProvider(
        impl: GradesGroupActivityProvider,
    ): GroupActivityProvider
}
