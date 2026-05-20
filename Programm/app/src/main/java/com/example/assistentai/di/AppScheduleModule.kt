package com.example.assistentai.di

import com.example.assistentai.schedule.DemoScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository
import ru.studentai.feature.schedule.integration.home.ScheduleUpcomingLessonProvider

/**
 * App-уровневые биндинги расписания:
 *
 *  • [ScheduleRepository] → [DemoScheduleRepository] (in-memory) — на момент отсутствия
 *    реального backend. При появлении сервера достаточно заменить эту строку на
 *    `ScheduleRepositoryImpl`, никаких изменений в feature_schedule или feature_home
 *    не потребуется.
 *
 *  • [UpcomingLessonProvider] → [ScheduleUpcomingLessonProvider] — это и есть подключение
 *    плагинной точки расширения feature_home. После этого binding'а на главном экране
 *    появляется реальная карточка «Ближайшая пара» с данными из расписания.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class AppScheduleModule {

    @Binds
    @Singleton
    public abstract fun bindScheduleRepository(impl: DemoScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    public abstract fun bindUpcomingLessonProvider(
        impl: ScheduleUpcomingLessonProvider,
    ): UpcomingLessonProvider
}
