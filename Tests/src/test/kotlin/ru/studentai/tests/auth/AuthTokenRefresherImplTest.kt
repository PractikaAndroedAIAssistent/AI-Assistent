package ru.studentai.tests.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.net.UnknownHostException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.isFailure
import ru.studentai.core.common.result.isSuccess
import ru.studentai.core.network.auth.RefreshedTokens
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.core.network.error.SafeApiCall
import ru.studentai.core.common.error.NetworkException
import ru.studentai.feature.auth.data.remote.api.AuthApi
import ru.studentai.feature.auth.data.remote.dto.RefreshRequestDto
import ru.studentai.feature.auth.data.remote.dto.TokensDto
import ru.studentai.feature.auth.data.repository.AuthTokenRefresherImpl
import ru.studentai.tests.auth.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class AuthTokenRefresherImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val safeApiCall = SafeApiCall(
        dispatchers = TestDispatcherProvider(dispatcher),
        errorMapper = HttpErrorMapper(),
    )
    private val api: AuthApi = mockk()

    private val sut = AuthTokenRefresherImpl(
        api = api,
        safeApiCall = safeApiCall,
    )

    @Test
    fun `refresh maps successful response to domain tokens`() = runTest(dispatcher) {
        coEvery { api.refresh(RefreshRequestDto("refresh-1")) } returns TokensDto(
            accessToken = "access-2",
            refreshToken = "refresh-2",
        )

        val result = sut.refresh("refresh-1")

        assertThat(result.isSuccess).isEqualTo(true)
        assertThat((result as DomainResult.Success).value).isEqualTo(
            RefreshedTokens(
                accessToken = "access-2",
                refreshToken = "refresh-2",
            ),
        )
        coVerify(exactly = 1) { api.refresh(RefreshRequestDto("refresh-1")) }
    }

    @Test
    fun `refresh maps api failure through SafeApiCall`() = runTest(dispatcher) {
        coEvery { api.refresh(any()) } throws UnknownHostException("offline")

        val result = sut.refresh("refresh-1")

        assertThat(result.isFailure).isEqualTo(true)
        assertThat((result as DomainResult.Failure).error).isInstanceOf(NetworkException.NoConnection::class)
    }
}
