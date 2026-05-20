package ru.studentai.tests.auth

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.AuthState
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.auth.domain.usecase.LoginUseCase
import ru.studentai.feature.auth.domain.usecase.LogoutUseCase
import ru.studentai.feature.auth.domain.usecase.ObserveAuthStateUseCase
import ru.studentai.feature.auth.domain.usecase.RegisterUseCase
import ru.studentai.feature.auth.domain.usecase.UpdateProfileUseCase
import ru.studentai.tests.auth.support.AuthFixtures
import ru.studentai.tests.auth.support.FakeAuthRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AuthUseCasesTest {

    @Test
    fun `login use case delegates credentials to repository`() = runTest {
        val repository = FakeAuthRepository().apply {
            loginResult = DomainResult.Success(AuthFixtures.user(email = "trimmed@example.com"))
        }
        val credentials = AuthCredentials("trimmed@example.com", "Abc12345")

        val result = LoginUseCase(repository)(credentials)

        assertThat(result).isEqualTo(repository.loginResult)
        assertThat(repository.loginCallCount).isEqualTo(1)
        assertThat(repository.lastLoginCredentials).isEqualTo(credentials)
    }

    @Test
    fun `register use case delegates registration data to repository`() = runTest {
        val repository = FakeAuthRepository().apply {
            registerResult = DomainResult.Success(AuthFixtures.user(role = UserRole.Teacher))
        }
        val data = RegistrationData(
            fullName = "Teacher 123",
            email = "teacher@example.com",
            password = "Abc12345",
            role = UserRole.Teacher,
            university = "MSU",
            course = 5,
        )

        val result = RegisterUseCase(repository)(data)

        assertThat(result).isEqualTo(repository.registerResult)
        assertThat(repository.registerCallCount).isEqualTo(1)
        assertThat(repository.lastRegistrationData).isEqualTo(data)
    }

    @Test
    fun `get profile use case returns repository profile result`() = runTest {
        val profile = AuthFixtures.profile(university = "BMSTU", course = 4)
        val repository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(profile)
        }

        val result = GetProfileUseCase(repository)()

        assertThat(result).isEqualTo(DomainResult.Success(profile))
        assertThat(repository.currentProfileCallCount).isEqualTo(1)
    }

    @Test
    fun `update profile use case delegates updated model to repository`() = runTest {
        val updatedProfile = AuthFixtures.profile(speciality = "Applied Math")
        val repository = FakeAuthRepository().apply {
            updateProfileResult = DomainResult.Success(updatedProfile)
        }

        val result = UpdateProfileUseCase(repository)(updatedProfile)

        assertThat(result).isEqualTo(DomainResult.Success(updatedProfile))
        assertThat(repository.updateProfileCallCount).isEqualTo(1)
        assertThat(repository.lastUpdatedProfile).isEqualTo(updatedProfile)
    }

    @Test
    fun `logout use case returns repository result`() = runTest {
        val repository = FakeAuthRepository().apply {
            logoutResult = DomainResult.Failure(AuthException.RefreshFailed())
        }

        val result = LogoutUseCase(repository)()

        assertThat(result).isEqualTo(repository.logoutResult)
        assertThat(repository.logoutCallCount).isEqualTo(1)
    }

    @Test
    fun `observe auth state use case exposes repository flow`() = runTest {
        val repository = FakeAuthRepository(initialAuthState = AuthState.Unauthenticated)
        val useCase = ObserveAuthStateUseCase(repository)

        useCase().test {
            assertThat(awaitItem()).isEqualTo(AuthState.Unauthenticated)
            repository.authStateFlow.value = AuthState.Authenticated
            assertThat(awaitItem()).isEqualTo(AuthState.Authenticated)
        }
    }
}
