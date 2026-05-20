package com.example.assistentai.di

import com.example.assistentai.tasks.DemoTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.tasks.domain.repository.TaskRepository
import ru.studentai.feature.tasks.integration.home.TasksNearestDeadlinesProvider
import ru.studentai.feature.tasks.integration.home.TasksTeacherTasksProvider

/**
 * App-уровневые биндинги задач:
 *
 *  • [TaskRepository] → [DemoTaskRepository] (in-memory) на время отсутствия backend.
 *  • Plugin-points feature_home — [NearestDeadlinesProvider] / [TeacherTasksProvider] —
 *    подключаются через адаптеры из feature_tasks.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class AppTasksModule {

    @Binds
    @Singleton
    public abstract fun bindTaskRepository(impl: DemoTaskRepository): TaskRepository

    @Binds
    @Singleton
    public abstract fun bindNearestDeadlinesProvider(
        impl: TasksNearestDeadlinesProvider,
    ): NearestDeadlinesProvider

    @Binds
    @Singleton
    public abstract fun bindTeacherTasksProvider(
        impl: TasksTeacherTasksProvider,
    ): TeacherTasksProvider
}
