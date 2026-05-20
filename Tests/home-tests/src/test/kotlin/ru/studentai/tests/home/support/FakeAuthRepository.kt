package ru.studentai.tests.home.support

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

    var currentProfileResult: DomainResult<UserProfile> = DomainResult.Success(HomeFixtures.studentProfile())
    var currentProfileCallCount: Int = 0

    override suspend fun login(credentials: AuthCredentials): DomainResult<User> =
        error("Not used in home tests")

    override suspend fun register(data: RegistrationData): DomainResult<User> =
        error("Not used in home tests")

    override suspend fun logout(): DomainResult<Unit> =
        error("Not used in home tests")

    override suspend fun currentProfile(): DomainResult<UserProfile> {
        currentProfileCallCount += 1
        return currentProfileResult
    }

    override suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile> =
        error("Not used in home tests")
}
