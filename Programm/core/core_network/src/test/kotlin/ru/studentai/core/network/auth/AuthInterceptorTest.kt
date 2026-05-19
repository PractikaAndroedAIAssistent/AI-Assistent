package ru.studentai.core.network.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthInterceptorTest {

    private lateinit var server: MockWebServer
    private val tokenStorage: AuthTokenStorage = mockk(relaxed = true)
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage))
            .build()
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `adds Bearer header when access token present`() {
        coEvery { tokenStorage.getAccessToken() } returns "TOKEN_123"
        server.enqueue(MockResponse().setResponseCode(200))

        client.newCall(Request.Builder().url(server.url("/me")).build()).execute().close()

        val recorded = server.takeRequest()
        assertThat(recorded.getHeader("Authorization")).isEqualTo("Bearer TOKEN_123")
    }

    @Test
    fun `omits Bearer header when no token`() {
        coEvery { tokenStorage.getAccessToken() } returns null
        server.enqueue(MockResponse().setResponseCode(200))

        client.newCall(Request.Builder().url(server.url("/public")).build()).execute().close()

        val recorded = server.takeRequest()
        assertThat(recorded.getHeader("Authorization")).isNull()
    }
}
