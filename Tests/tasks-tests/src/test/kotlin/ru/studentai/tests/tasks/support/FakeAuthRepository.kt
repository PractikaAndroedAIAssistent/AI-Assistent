package ru.studentai.tests.tasks.support

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.AuthState
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.repository.AuthRepository

internal class FakeAuthRepository(
    initialAuthState: AuthState = AuthState.Unauthenticated,
) : AuthRepository {

    val authStateFlow: MutableStateFlow<AuthState> = MutableStateFlow(initialAuthState)
    override val authState: Flow<AuthState> = authStateFlow

    var loginResult: DomainResult<User> = DomainResult.Success(TaskFixtures.studentUser())
    var registerResult: DomainResult<User> = DomainResult.Success(TaskFixtures.studentUser())
    var logoutResult: DomainResult<Unit> = DomainResult.Success(Unit)
    var currentProfileResult: DomainResult<UserProfile> =
        DomainResult.Success(TaskFixtures.studentProfile())
    var updateProfileResult: DomainResult<UserProfile> =
        DomainResult.Success(TaskFixtures.studentProfile())

    var currentProfileCallCount: Int = 0

    override suspend fun login(credentials: AuthCredentials): DomainResult<User> = loginResult

    override suspend fun register(data: RegistrationData): DomainResult<User> = registerResult

    override suspend fun logout(): DomainResult<Unit> = logoutResult

    override suspend fun currentProfile(): DomainResult<UserProfile> {
        currentProfileCallCount += 1
        return currentProfileResult
    }

    override suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile> =
        updateProfileResult
}
