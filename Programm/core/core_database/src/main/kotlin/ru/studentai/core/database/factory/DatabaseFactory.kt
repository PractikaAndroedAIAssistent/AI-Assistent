package ru.studentai.core.database.factory

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import ru.studentai.core.database.migration.MigrationContainer

/**
 * Фабрика для создания [RoomDatabase] с дефолтной конфигурацией проекта.
 *
 * Конвенция имён БД: `studentai_<feature>.db` (например, `studentai_auth.db`).
 *
 * Каждая фича создаёт свой Database через эту фабрику. Пример:
 *
 * ```
 * @Provides
 * @Singleton
 * fun provideScheduleDatabase(factory: DatabaseFactory): ScheduleDatabase =
 *     factory.build(name = "studentai_schedule.db", migrations = ScheduleMigrations.all())
 * ```
 */
@Singleton
public class DatabaseFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * @param name        имя файла БД (с расширением .db)
     * @param migrations  миграции, в строгом порядке возрастания версий
     * @param configure   дополнительный конфиг (callbacks, query callback, encryption)
     */
    public inline fun <reified T : RoomDatabase> build(
        name: String,
        migrations: Array<Migration> = emptyArray(),
        noinline configure: RoomDatabase.Builder<T>.() -> Unit = {},
    ): T = buildInternal(T::class.java, name, migrations, configure)

    /** Версия с [MigrationContainer]. */
    public inline fun <reified T : RoomDatabase> build(
        name: String,
        container: MigrationContainer,
        noinline configure: RoomDatabase.Builder<T>.() -> Unit = {},
    ): T = build<T>(name = name, migrations = container.all(), configure = configure)

    public fun <T : RoomDatabase> buildInternal(
        klass: Class<T>,
        name: String,
        migrations: Array<Migration>,
        configure: RoomDatabase.Builder<T>.() -> Unit,
    ): T = Room.databaseBuilder(context, klass, name)
        .addMigrations(*migrations)
        // Намеренно НЕ вызываем fallbackToDestructiveMigration() — данные пользователя
        // не должны теряться автоматически (release). Если фича добавляет downgrade-fallback,
        // она делает это явно в [configure]-лямбде.
        .apply(configure)
        .build()
}
