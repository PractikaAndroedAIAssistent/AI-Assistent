package ru.studentai.feature.schedule.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.create
import ru.studentai.core.database.factory.DatabaseFactory
import ru.studentai.feature.schedule.data.local.dao.ScheduleDao
import ru.studentai.feature.schedule.data.local.dao.SubjectDao
import ru.studentai.feature.schedule.data.local.database.ScheduleDatabase
import ru.studentai.feature.schedule.data.remote.api.ScheduleApi

/**
 * Hilt-биндинги data-слоя feature_schedule.
 *
 * ВАЖНО: `ScheduleRepository` НЕ биндится здесь — app выбирает реализацию
 * (production [ru.studentai.feature.schedule.data.repository.ScheduleRepositoryImpl]
 * или demo [com.example.assistentai.schedule.DemoScheduleRepository]).
 *
 * Тот же приём, что для AuthRepository — даёт гибкую замену без правок фичи.
 */
@Module
@InstallIn(SingletonComponent::class)
public object ScheduleDataModule {

    @Provides
    @Singleton
    public fun provideScheduleDatabase(factory: DatabaseFactory): ScheduleDatabase =
        factory.build(name = ScheduleDatabase.NAME)

    @Provides
    @Singleton
    public fun provideScheduleDao(database: ScheduleDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    @Singleton
    public fun provideSubjectDao(database: ScheduleDatabase): SubjectDao = database.subjectDao()

    @Provides
    @Singleton
    public fun provideScheduleApi(retrofit: Retrofit): ScheduleApi = retrofit.create()
}
