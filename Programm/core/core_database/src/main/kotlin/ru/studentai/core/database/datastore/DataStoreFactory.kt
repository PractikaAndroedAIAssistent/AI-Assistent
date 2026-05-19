package ru.studentai.core.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import ru.studentai.core.common.dispatchers.DispatcherProvider

/**
 * Фабрика для создания [DataStore]<[Preferences]> по имени файла.
 *
 * Каждая фича создаёт свой DataStore (например, `settings.preferences_pb`) через эту фабрику.
 * Для типизированного proto-DataStore фича сама подключает codegen-плагин.
 *
 * Корутинный scope:
 *  • используется [SupervisorJob] + [DispatcherProvider.io];
 *  • даёт устойчивость к падению одной записи (не валит весь DataStore).
 */
@Singleton
public class DataStoreFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider,
) {

    /**
     * @param name имя файла без расширения. Расширение `.preferences_pb` добавится автоматически.
     */
    public fun createPreferences(name: String): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + dispatchers.io),
            produceFile = { context.preferencesDataStoreFile(name) },
        )
}
