package com.example.assistentai.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.AuthState
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.repository.AuthRepository

/**
 * Demo-реализация [AuthRepository] для запуска приложения без реального backend.
 *
 * Сохраняет «авторизованный» профиль в памяти ([currentProfile]). Этого достаточно,
 * чтобы пройти flow Login → Welcome → Logout и наглядно убедиться, что архитектура
 * фич, навигации и MVI работает.
 *
 * При появлении реального backend этот биндинг заменяется в
 * [com.example.assistentai.di.AppAuthModule] на `AuthRepositoryImpl` —
 * никаких изменений в feature_auth или вызывающих сайтах не потребуется.
 */
@Singleton
public class DemoAuthRepository @Inject constructor() : AuthRepository {

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    private var currentProfile: UserProfile? = null

    override val authState: Flow<AuthState> = authStateFlow.asStateFlow()

    override suspend fun login(credentials: AuthCredentials): DomainResult<User> {
        delay(SIMULATED_NETWORK_DELAY_MS)
        if (credentials.email == FAIL_EMAIL) {
            return DomainResult.Failure(AuthException.InvalidCredentials())
        }
        val user = User(
            id = "demo-user",
            email = credentials.email,
            fullName = "Демо-пользователь",
            role = UserRole.Student,
        )
        currentProfile = UserProfile(
            user = user,
            university = "Демо-университет",
            group = "ДЕМО-101",
            course = 1,
            speciality = "Информатика и вычислительная техника",
        )
        authStateFlow.value = AuthState.Authenticated
        return DomainResult.Success(user)
    }

    override suspend fun register(data: RegistrationData): DomainResult<User> {
        delay(SIMULATED_NETWORK_DELAY_MS)
        val user = User(
            id = "demo-user",
            email = data.email,
            fullName = data.fullName,
            role = data.role,
        )
        currentProfile = UserProfile(
            user = user,
            university = data.university,
            group = data.group,
            course = data.course,
            speciality = data.speciality,
        )
        authStateFlow.value = AuthState.Authenticated
        return DomainResult.Success(user)
    }

    override suspend fun logout(): DomainResult<Unit> {
        delay(SIMULATED_NETWORK_DELAY_MS / 2)
        currentProfile = null
        authStateFlow.value = AuthState.Unauthenticated
        return DomainResult.Success(Unit)
    }

    override suspend fun currentProfile(): DomainResult<UserProfile> {
        delay(SIMULATED_NETWORK_DELAY_MS / 2)
        return currentProfile?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AuthException.Unauthorized())
    }

    override suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile> {
        delay(SIMULATED_NETWORK_DELAY_MS)
        currentProfile = profile
        return DomainResult.Success(profile)
    }

    private companion object {
        const val SIMULATED_NETWORK_DELAY_MS = 600L
        const val FAIL_EMAIL = "fail@test.com"
    }
}
