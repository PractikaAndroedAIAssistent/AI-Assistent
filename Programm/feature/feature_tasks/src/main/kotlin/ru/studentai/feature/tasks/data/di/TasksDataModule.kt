package ru.studentai.feature.tasks.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.create
import ru.studentai.core.database.factory.DatabaseFactory
import ru.studentai.feature.tasks.data.local.dao.TaskDao
import ru.studentai.feature.tasks.data.local.database.TaskDatabase
import ru.studentai.feature.tasks.data.remote.api.TasksApi

/**
 * Hilt-биндинги data-слоя feature_tasks.
 *
 * `TaskRepository` НЕ биндится здесь — app выбирает реализацию
 * (production TaskRepositoryImpl или demo).
 */
@Module
@InstallIn(SingletonComponent::class)
public object TasksDataModule {

    @Provides
    @Singleton
    public fun provideTaskDatabase(factory: DatabaseFactory): TaskDatabase =
        factory.build(name = TaskDatabase.NAME)

    @Provides
    @Singleton
    public fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    public fun provideTasksApi(retrofit: Retrofit): TasksApi = retrofit.create()
}
