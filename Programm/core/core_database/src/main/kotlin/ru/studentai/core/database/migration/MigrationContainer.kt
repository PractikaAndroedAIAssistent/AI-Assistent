package ru.studentai.core.database.migration

import androidx.room.migration.Migration

/**
 * Контейнер миграций для одной [androidx.room.RoomDatabase].
 *
 * Каждая фича создаёт свой `MigrationContainer` (или просто List<Migration>),
 * который передаётся в [ru.studentai.core.database.factory.DatabaseFactory.build].
 *
 * Полезен, когда миграций много и хочется их хранить рядом с базой, а не в DI-модуле.
 */
public class MigrationContainer(
    private val migrations: List<Migration>,
) {
    public constructor(vararg migrations: Migration) : this(migrations.toList())

    /** Возвращает все миграции для билдера. */
    public fun all(): Array<Migration> = migrations.toTypedArray()

    /** Возвращает миграцию между конкретными версиями, либо `null`. */
    public fun between(from: Int, to: Int): Migration? =
        migrations.firstOrNull { it.startVersion == from && it.endVersion == to }
}
