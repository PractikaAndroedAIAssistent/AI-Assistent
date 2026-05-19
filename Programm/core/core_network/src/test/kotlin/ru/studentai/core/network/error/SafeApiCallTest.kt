package ru.studentai.core.network.error

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import java.net.UnknownHostException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.result.DomainResult

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = dispatcher
        override val mainImmediate: CoroutineDispatcher = dispatcher
        override val io: CoroutineDispatcher = dispatcher
        override val default: CoroutineDispatcher = dispatcher
        override val unconfined: CoroutineDispatcher = dispatcher
    }
    private val sut = SafeApiCall(dispatchers, HttpErrorMapper())

    @Test
    fun `success path returns DomainResult Success`() = runTest(dispatcher) {
        val result = sut { 42 }
        assertThat(result).isInstanceOf(DomainResult.Success::class)
        assertThat((result as DomainResult.Success).value).isEqualTo(42)
    }

    @Test
    fun `UnknownHostException maps to NetworkException NoConnection`() = runTest(dispatcher) {
        val result: DomainResult<Int> = sut { throw UnknownHostException("no dns") }
        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        assertThat((result as DomainResult.Failure).error).isInstanceOf(NetworkException.NoConnection::class)
    }

    @Test
    fun `CancellationException is rethrown without wrapping`() = runTest(dispatcher) {
        assertThrows<kotlinx.coroutines.CancellationException> {
            sut<Int> { throw kotlinx.coroutines.CancellationException("cancel") }
        }
    }
}
