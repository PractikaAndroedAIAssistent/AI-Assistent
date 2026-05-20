package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.network.auth.AuthState
import ru.studentai.feature.auth.domain.repository.AuthRepository

/**
 * Наблюдатель сессии. Используется в `app/StudentAiApp` для выбора стартового экрана
 * (Login vs Home) и обработки разлогина при истечении токена.
 */
public class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public operator fun invoke(): Flow<AuthState> = repository.authState
}
