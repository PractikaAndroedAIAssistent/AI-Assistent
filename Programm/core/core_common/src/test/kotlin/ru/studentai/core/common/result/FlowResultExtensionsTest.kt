package ru.studentai.core.common.result

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameInstanceAs
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.UnknownException

class FlowResultExtensionsTest {

    @Test
    fun `asDomainResult wraps values in Success`() = runTest {
        flowOf(1, 2, 3)
            .asDomainResult()
            .test {
                assertThat(awaitItem().getOrNull()).isEqualTo(1)
                assertThat(awaitItem().getOrNull()).isEqualTo(2)
                assertThat(awaitItem().getOrNull()).isEqualTo(3)
                awaitComplete()
            }
    }

    @Test
    fun `asDomainResult maps AppException to Failure preserving instance`() = runTest {
        val err = NetworkException.Timeout()
        flow {
            emit(1)
            throw err
        }.asDomainResult().test {
            assertThat(awaitItem().getOrNull()).isEqualTo(1)
            assertThat(awaitItem().errorOrNull()).isSameInstanceAs(err)
            awaitComplete()
        }
    }

    @Test
    fun `asDomainResult maps generic throwable to UnknownException`() = runTest {
        flow<Int> {
            throw IllegalStateException("kaboom")
        }.asDomainResult().test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(DomainResult.Failure::class)
            assertThat(item.errorOrNull()!!).isInstanceOf(UnknownException::class)
            assertThat(item.errorOrNull()!!.message).isEqualTo("kaboom")
            awaitComplete()
        }
    }

    @Test
    fun `asDomainResultWithLoading runs onStart before emissions`() = runTest {
        var started = false
        flowOf(10)
            .asDomainResultWithLoading { started = true }
            .test {
                assertThat(started).isEqualTo(true)
                assertThat(awaitItem().getOrNull()).isEqualTo(10)
                awaitComplete()
            }
    }

    @Test
    fun `DomainResult asFlow emits single item`() = runTest {
        DomainResult.Success(7).asFlow().test {
            assertThat(awaitItem().getOrNull()).isEqualTo(7)
            awaitComplete()
        }
    }
}
