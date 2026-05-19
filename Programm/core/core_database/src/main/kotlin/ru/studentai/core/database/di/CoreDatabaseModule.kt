package ru.studentai.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.core.database.converter.DurationConverter
import ru.studentai.core.database.converter.InstantConverter
import ru.studentai.core.database.converter.LocalDateConverter
import ru.studentai.core.database.converter.LocalDateTimeConverter
import ru.studentai.core.database.converter.StringListConverter
import ru.studentai.core.database.converter.UuidConverter

/**
 * Hilt-модуль `core_database`.
 *
 * Намеренно НЕ предоставляет конкретные `RoomDatabase` и DAO — это ответственность
 * каждой фичи. См. `DatabaseFactory` и `DataStoreFactory` (они автоинжектируются).
 *
 * Здесь регистрируются singleton-экземпляры type converter'ов — Room сам создаёт их
 * при необходимости, но если фича захочет переиспользовать converter вне Room
 * (например, в repository), она получит singleton через DI.
 */
@Module
@InstallIn(SingletonComponent::class)
public object CoreDatabaseProviders {

    @Provides
    @Singleton
    public fun provideInstantConverter(): InstantConverter = InstantConverter()

    @Provides
    @Singleton
    public fun provideLocalDateConverter(): LocalDateConverter = LocalDateConverter()

    @Provides
    @Singleton
    public fun provideLocalDateTimeConverter(): LocalDateTimeConverter = LocalDateTimeConverter()

    @Provides
    @Singleton
    public fun provideDurationConverter(): DurationConverter = DurationConverter()

    @Provides
    @Singleton
    public fun provideUuidConverter(): UuidConverter = UuidConverter()

    @Provides
    @Singleton
    public fun provideStringListConverter(): StringListConverter = StringListConverter()
}
