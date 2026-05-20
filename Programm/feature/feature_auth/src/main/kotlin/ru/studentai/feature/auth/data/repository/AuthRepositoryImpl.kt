package ru.studentai.feature.auth.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.flatMap
import ru.studentai.core.common.result.map
import ru.studentai.core.common.result.onSuccess
import ru.studentai.core.network.auth.AuthState
import ru.studentai.core.network.auth.AuthTokenStorage
import ru.studentai.core.network.error.SafeApiCall
import ru.studentai.feature.auth.data.mapper.RegistrationMapper
import ru.studentai.feature.auth.data.mapper.UserMapper
import ru.studentai.feature.auth.data.remote.api.AuthApi
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.repository.AuthRepository

/**
 * Реализация [AuthRepository].
 *
 * Оркестрирует:
 *  • [AuthApi] — REST-запросы (через [SafeApiCall]);
 *  • [AuthTokenStorage] — сохранение/очистка JWT;
 *  • mappers — DTO ↔ domain.
 *
 * Контракт logout: серверный вызов и локальная очистка независимы.
 * Если серверный вызов упал (например, нет сети), мы всё равно очищаем локальные токены —
 * пользователь не должен «застрять» в залогиненном состоянии из-за сетевой ошибки.
 */
@Singleton
public class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: AuthTokenStorage,
    private val safeApiCall: SafeApiCall,
    private val userMapper: UserMapper,
    private val registrationMapper: RegistrationMapper,
) : AuthRepository {

    override val authState: Flow<AuthState> = tokenStorage.authState

    override suspend fun login(credentials: AuthCredentials): DomainResult<User> =
        safeApiCall {
            api.login(registrationMapper.toLoginDto(credentials))
        }.flatMap { response ->
            tokenStorage.saveTokens(
                accessToken = response.tokens.accessToken,
                refreshToken = response.tokens.refreshToken,
            )
            DomainResult.Success(userMapper.toDomain(response.user))
        }

    override suspend fun register(data: RegistrationData): DomainResult<User> =
        safeApiCall {
            api.register(registrationMapper.toRegisterDto(data))
        }.flatMap { response ->
            tokenStorage.saveTokens(
                accessToken = response.tokens.accessToken,
                refreshToken = response.tokens.refreshToken,
            )
            DomainResult.Success(userMapper.toDomain(response.user))
        }

    override suspend fun logout(): DomainResult<Unit> {
        // Серверный logout — best-effort. Локально очищаем токены в любом случае.
        val serverResult = safeApiCall { api.logout() }
        tokenStorage.clear()
        return serverResult.map { Unit }
    }

    override suspend fun currentProfile(): DomainResult<UserProfile> =
        safeApiCall { api.currentProfile() }
            .map(userMapper::toProfile)

    override suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile> =
        safeApiCall {
            api.updateProfile(userMapper.toUpdateRequest(profile))
        }.map(userMapper::toProfile)
}
