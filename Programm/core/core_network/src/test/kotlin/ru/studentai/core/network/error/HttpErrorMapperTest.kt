package ru.studentai.core.network.error

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.error.NetworkException

class HttpErrorMapperTest {

    private val sut = HttpErrorMapper()

    @Test
    fun `CancellationException is rethrown without wrapping`() {
        val ce = CancellationException("cancel")
        val thrown = assertThrows<CancellationException> { sut.map(ce) }
        assertThat(thrown.message).isEqualTo("cancel")
    }

    @Test
    fun `UnknownHostException maps to NoConnection`() {
        assertThat(sut.map(UnknownHostException("no dns")))
            .isInstanceOf(NetworkException.NoConnection::class)
    }

    @Test
    fun `ConnectException maps to NoConnection`() {
        assertThat(sut.map(ConnectException("refused")))
            .isInstanceOf(NetworkException.NoConnection::class)
    }

    @Test
    fun `SocketTimeoutException maps to Timeout`() {
        assertThat(sut.map(SocketTimeoutException("slow")))
            .isInstanceOf(NetworkException.Timeout::class)
    }

    @Test
    fun `Generic IOException maps to NoConnection`() {
        assertThat(sut.map(IOException("io")))
            .isInstanceOf(NetworkException.NoConnection::class)
    }

    @Test
    fun `SerializationException maps to NetworkException Serialization`() {
        assertThat(sut.map(SerializationException("bad json")))
            .isInstanceOf(NetworkException.Serialization::class)
    }

    @Test
    fun `HttpException 401 maps to AuthException Unauthorized`() {
        val ex = http(code = 401)
        assertThat(sut.map(ex)).isInstanceOf(AuthException.Unauthorized::class)
    }

    @Test
    fun `HttpException 403 maps to AuthException Forbidden`() {
        val ex = http(code = 403)
        assertThat(sut.map(ex)).isInstanceOf(AuthException.Forbidden::class)
    }

    @Test
    fun `HttpException 404 maps to NetworkException Http with code`() {
        val mapped = sut.map(http(code = 404, body = "{\"error\":\"not_found\"}"))
        assertThat(mapped).isInstanceOf(NetworkException.Http::class)
        assertThat((mapped as NetworkException.Http).code).isEqualTo(404)
    }

    @Test
    fun `HttpException 500 maps to NetworkException Server with code`() {
        val mapped = sut.map(http(code = 500, body = "oops"))
        assertThat(mapped).isInstanceOf(NetworkException.Server::class)
        assertThat((mapped as NetworkException.Server).code).isEqualTo(500)
    }

    @Test
    fun `HttpException 503 maps to NetworkException Server`() {
        val mapped = sut.map(http(code = 503))
        assertThat(mapped).isInstanceOf(NetworkException.Server::class)
    }

    @Test
    fun `Existing AppException is returned as-is without re-wrapping`() {
        val original = NetworkException.NoConnection()
        assertThat(sut.map(original)).isEqualTo(original as Throwable)
    }

    private fun http(code: Int, body: String = ""): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        val response: Response<Unit> = Response.error(code, responseBody)
        // HttpException требует raw Response — используем mock
        val ex = mockk<HttpException>(relaxed = true)
        every { ex.code() } returns code
        every { ex.response() } returns response
        every { ex.message() } returns "HTTP $code"
        return ex
    }
}
