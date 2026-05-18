package ru.studentai.core.common.result

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isSameInstanceAs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.UnknownException

class SafeCallTest {

    @Test
    fun `safeCall returns Success when block completes`() = runTest {
        val r = safeCall { 42 }
        assertThat(r).isInstanceOf(DomainResult.Success::class)
        assertThat(r.getOrNull()).isEqualTo(42)
    }

    @Test
    fun `safeCall maps AppException to Failure preserving instance`() = runTest {
        val err = NetworkException.Timeout()
        val r: DomainResult<Int> = safeCall { throw err }
        assertThat(r.errorOrNull()).isSameInstanceAs(err as AppException)
    }

    @Test
    fun `safeCall wraps unknown throwable in UnknownException`() = runTest {
        val r: DomainResult<Int> = safeCall { throw IllegalStateException("boom") }
        val error = r.errorOrNull()
        assertThat(error).isNotNull()
        assertThat(error!!).isInstanceOf(UnknownException::class)
        assertThat(error.message).isEqualTo("boom")
    }

    @Test
    fun `safeCall rethrows CancellationException without wrapping`() = runTest {
        val original = CancellationException("manual-cancel")
        val thrown = assertThrows<CancellationException> {
            safeCall<Int> { throw original }
        }
        assertThat(thrown).isSameInstanceAs(original)
    }

    @Test
    fun `safeCall does not swallow CancellationException subclasses`() = runTest {
        class CustomCancellation : CancellationException("custom")
        val custom = CustomCancellation()
        val thrown = assertThrows<CancellationException> {
            safeCall<Int> { throw custom }
        }
        assertThat(thrown).isSameInstanceAs(custom as CancellationException)
    }

    @Test
    fun `safeCallMapping uses custom mapper for unknown throwable`() = runTest {
        val r: DomainResult<Int> = safeCallMapping(
            mapper = { NetworkException.Server(code = 500, message = it.message) },
        ) {
            throw RuntimeException("server died")
        }
        val err = r.errorOrNull()
        assertThat(err).isNotNull()
        assertThat(err!!).isInstanceOf(NetworkException.Server::class)
    }

    @Test
    fun `safeCallMapping still preserves AppException without mapping`() = runTest {
        val original = NetworkException.NoConnection()
        val r: DomainResult<Int> = safeCallMapping(
            mapper = { UnknownException(it.message, it) },
        ) {
            throw original
        }
        assertThat(r.errorOrNull()).isSameInstanceAs(original as AppException)
    }

    @Test
    fun `safeCallMapping also rethrows CancellationException`() = runTest {
        val ce = CancellationException("propagate")
        val thrown = assertThrows<CancellationException> {
            safeCallMapping<Int>(mapper = { UnknownException(cause = it) }) { throw ce }
        }
        assertThat(thrown).isSameInstanceAs(ce)
    }
}
