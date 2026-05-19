package ru.studentai.core.network.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.studentai.core.common.dispatchers.DispatcherProvider

/**
 * Реализация [AuthTokenStorage] поверх [EncryptedSharedPreferences].
 *
 * Безопасность (ТЗ §4.1.5):
 *  • ключ шифрования генерируется и хранится в Android Keystore (AES256_GCM master key);
 *  • значения шифруются `AES256_GCM`, ключи — `AES256_SIV` (детерминированно, чтобы можно искать);
 *  • никаких операций на main-потоке: всё через [DispatcherProvider.io].
 *
 * Атомарность:
 *  • запись/чтение защищены [Mutex] — параллельные refresh-цепочки не конфликтуют.
 */
@Singleton
public class EncryptedAuthTokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider,
) : AuthTokenStorage {

    private val mutex = Mutex()

    private val prefs: SharedPreferences by lazy { createEncryptedPrefs() }

    private val _authState: MutableStateFlow<AuthState> = MutableStateFlow(initialAuthState())
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun getAccessToken(): String? = withContext(dispatchers.io) {
        mutex.withLock { prefs.getString(KEY_ACCESS_TOKEN, null) }
    }

    override suspend fun getRefreshToken(): String? = withContext(dispatchers.io) {
        mutex.withLock { prefs.getString(KEY_REFRESH_TOKEN, null) }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String): Unit =
        withContext(dispatchers.io) {
            mutex.withLock {
                prefs.edit()
                    .putString(KEY_ACCESS_TOKEN, accessToken)
                    .putString(KEY_REFRESH_TOKEN, refreshToken)
                    .commit()
            }
            _authState.value = AuthState.Authenticated
        }

    override suspend fun clear(): Unit = withContext(dispatchers.io) {
        mutex.withLock {
            prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .commit()
        }
        _authState.value = AuthState.Unauthenticated
    }

    private fun initialAuthState(): AuthState =
        if (prefs.contains(KEY_ACCESS_TOKEN)) AuthState.Authenticated else AuthState.Unauthenticated

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private companion object {
        const val FILE_NAME = "ru.studentai.auth_tokens"
        const val MASTER_KEY_ALIAS = "ru.studentai.auth_master_key"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
