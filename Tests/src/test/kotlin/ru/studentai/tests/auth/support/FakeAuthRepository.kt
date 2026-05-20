package ru.studentai.tests.auth.support

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

    var loginResult: DomainResult<User> = DomainResult.Success(AuthFixtures.user())
    var loginCallCount: Int = 0
    var lastLoginCredentials: AuthCredentials? = null

    var registerResult: DomainResult<User> = DomainResult.Success(AuthFixtures.user())
    var registerCallCount: Int = 0
    var lastRegistrationData: RegistrationData? = null

    var logoutResult: DomainResult<Unit> = DomainResult.Success(Unit)
    var logoutCallCount: Int = 0

    var currentProfileResult: DomainResult<UserProfile> = DomainResult.Success(AuthFixtures.profile())
    var currentProfileCallCount: Int = 0

    var updateProfileResult: DomainResult<UserProfile> = DomainResult.Success(AuthFixtures.profile())
    var updateProfileCallCount: Int = 0
    var lastUpdatedProfile: UserProfile? = null

    override suspend fun login(credentials: AuthCredentials): DomainResult<User> {
        loginCallCount += 1
        lastLoginCredentials = credentials
        return loginResult
    }

    override suspend fun register(data: RegistrationData): DomainResult<User> {
        registerCallCount += 1
        lastRegistrationData = data
        return registerResult
    }

    override suspend fun logout(): DomainResult<Unit> {
        logoutCallCount += 1
        return logoutResult
    }

    override suspend fun currentProfile(): DomainResult<UserProfile> {
        currentProfileCallCount += 1
        return currentProfileResult
    }

    override suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile> {
        updateProfileCallCount += 1
        lastUpdatedProfile = profile
        return updateProfileResult
    }
}
