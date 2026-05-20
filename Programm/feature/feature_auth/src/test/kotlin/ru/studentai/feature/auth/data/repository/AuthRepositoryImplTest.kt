package ru.studentai.feature.auth.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import java.net.UnknownHostException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.AuthState
import ru.studentai.core.network.auth.AuthTokenStorage
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.core.network.error.SafeApiCall
import ru.studentai.feature.auth.data.mapper.RegistrationMapper
import ru.studentai.feature.auth.data.mapper.UserMapper
import ru.studentai.feature.auth.data.remote.api.AuthApi
import ru.studentai.feature.auth.data.remote.dto.AuthResponseDto
import ru.studentai.feature.auth.data.remote.dto.TokensDto
import ru.studentai.feature.auth.data.remote.dto.UserDto
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.UserRole

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = dispatcher
        override val mainImmediate: CoroutineDispatcher = dispatcher
        override val io: CoroutineDispatcher = dispatcher
        override val default: CoroutineDispatcher = dispatcher
        override val unconfined: CoroutineDispatcher = dispatcher
    }
    private val safeApiCall = SafeApiCall(dispatchers, HttpErrorMapper())

    private val api: AuthApi = mockk()
    private val storage: AuthTokenStorage = mockk {
        coEvery { authState } returns MutableStateFlow(AuthState.Unauthenticated)
    }

    private val sut = AuthRepositoryImpl(
        api = api,
        tokenStorage = storage,
        safeApiCall = safeApiCall,
        userMapper = UserMapper(),
        registrationMapper = RegistrationMapper(),
    )

    @Test
    fun `login success saves tokens and returns user`() = runTest(dispatcher) {
        val response = AuthResponseDto(
            tokens = TokensDto("ACCESS", "REFRESH"),
            user = UserDto(id = "u1", email = "i@v.ru", fullName = "Иван", role = "student"),
        )
        coEvery { api.login(any()) } returns response
        coEvery { storage.saveTokens(any(), any()) } just Runs

        val result = sut.login(AuthCredentials("i@v.ru", "Abc12345"))
        assertThat(result).isInstanceOf(DomainResult.Success::class)
        assertThat((result as DomainResult.Success).value.role).isEqualTo(UserRole.Student as UserRole)
        coVerify(exactly = 1) { storage.saveTokens("ACCESS", "REFRESH") }
    }

    @Test
    fun `login network failure does NOT save tokens`() = runTest(dispatcher) {
        coEvery { api.login(any()) } throws UnknownHostException("offline")

        val result = sut.login(AuthCredentials("i@v.ru", "Abc12345"))
        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        assertThat((result as DomainResult.Failure).error).isInstanceOf(NetworkException.NoConnection::class)
        coVerify(exactly = 0) { storage.saveTokens(any(), any()) }
    }

    @Test
    fun `logout clears local tokens even when server call fails`() = runTest(dispatcher) {
        coEvery { api.logout() } throws UnknownHostException("offline")
        coEvery { storage.clear() } just Runs

        val result = sut.logout()
        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        coVerify(exactly = 1) { storage.clear() }
    }

    @Test
    fun `logout clears local tokens on success too`() = runTest(dispatcher) {
        coEvery { api.logout() } just Runs
        coEvery { storage.clear() } just Runs

        val result = sut.logout()
        assertThat(result).isInstanceOf(DomainResult.Success::class)
        coVerify(exactly = 1) { storage.clear() }
    }

    @Test
    fun `currentProfile maps 401 to Unauthorized via HttpErrorMapper`() = runTest(dispatcher) {
        val httpEx: retrofit2.HttpException = mockk(relaxed = true)
        io.mockk.every { httpEx.code() } returns 401
        io.mockk.every { httpEx.response() } returns null
        coEvery { api.currentProfile() } throws httpEx

        val result = sut.currentProfile()
        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        assertThat((result as DomainResult.Failure).error).isInstanceOf(AuthException.Unauthorized::class)
    }
}
