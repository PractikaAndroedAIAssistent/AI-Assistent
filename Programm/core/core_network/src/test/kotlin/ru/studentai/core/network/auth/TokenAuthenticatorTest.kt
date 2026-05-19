package ru.studentai.core.network.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.result.DomainResult

class TokenAuthenticatorTest {

    private lateinit var server: MockWebServer
    private val storage: AuthTokenStorage = mockk(relaxed = true)
    private val refresher: TokenRefresher = mockk()
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        clearAllMocks()
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(storage))
            .authenticator(TokenAuthenticator(storage, refresher))
            .build()
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `on 401 refreshes token and retries request with new bearer`() {
        // Последовательность чтений getAccessToken:
        //  1) AuthInterceptor для первого запроса → "OLD"
        //  2) TokenAuthenticator сверяет fresh-token в storage → "OLD" (никто параллельно не обновил)
        //  3) AuthInterceptor для retry-запроса → "NEW" (уже сохранён рефрешем)
        coEvery { storage.getAccessToken() } returnsMany listOf("OLD", "OLD", "NEW")
        coEvery { storage.getRefreshToken() } returns "REFRESH"
        coEvery { refresher.refresh("REFRESH") } returns DomainResult.Success(
            RefreshedTokens(accessToken = "NEW", refreshToken = "REFRESH_NEW"),
        )
        coEvery { storage.saveTokens("NEW", "REFRESH_NEW") } just Runs

        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(Request.Builder().url(server.url("/me")).build()).execute()
        response.use {
            assertThat(it.code).isEqualTo(200)
        }

        val first = server.takeRequest()
        val second = server.takeRequest()
        assertThat(first.getHeader("Authorization")).isEqualTo("Bearer OLD")
        assertThat(second.getHeader("Authorization")).isEqualTo("Bearer NEW")
        assertThat(second.getHeader("X-Auth-Retried")).isEqualTo("true")
        coVerify(exactly = 1) { refresher.refresh("REFRESH") }
        coVerify(exactly = 1) { storage.saveTokens("NEW", "REFRESH_NEW") }
    }

    @Test
    fun `on refresh failure clears storage and gives up`() {
        coEvery { storage.getAccessToken() } returns "OLD"
        coEvery { storage.getRefreshToken() } returns "REFRESH"
        coEvery { refresher.refresh("REFRESH") } returns DomainResult.Failure(
            AuthException.RefreshFailed(),
        )
        coEvery { storage.clear() } just Runs

        server.enqueue(MockResponse().setResponseCode(401))

        val response = client.newCall(Request.Builder().url(server.url("/me")).build()).execute()
        response.use {
            assertThat(it.code).isEqualTo(401)
        }
        coVerify(exactly = 1) { storage.clear() }
    }

    @Test
    fun `when no refresh token available — gives up immediately`() {
        coEvery { storage.getAccessToken() } returns "OLD"
        coEvery { storage.getRefreshToken() } returns null

        server.enqueue(MockResponse().setResponseCode(401))

        val response = client.newCall(Request.Builder().url(server.url("/me")).build()).execute()
        response.use {
            assertThat(it.code).isEqualTo(401)
        }
        coVerify(exactly = 0) { refresher.refresh(any()) }
    }

    @Test
    fun `does not retry indefinitely if refreshed request still returns 401`() {
        // 1) AuthInterceptor #1 → OLD; 2) Authenticator fresh-check → OLD; 3) AuthInterceptor #2 → NEW
        coEvery { storage.getAccessToken() } returnsMany listOf("OLD", "OLD", "NEW")
        coEvery { storage.getRefreshToken() } returns "REFRESH"
        coEvery { refresher.refresh("REFRESH") } returns DomainResult.Success(
            RefreshedTokens("NEW", "REFRESH_NEW"),
        )
        coEvery { storage.saveTokens(any(), any()) } just Runs

        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(401)) // повторная попытка тоже 401

        val response = client.newCall(Request.Builder().url(server.url("/me")).build()).execute()
        response.use {
            assertThat(it.code).isEqualTo(401)
        }
        // refresh должен быть вызван ровно один раз — циклов нет
        coVerify(exactly = 1) { refresher.refresh("REFRESH") }
    }
}
