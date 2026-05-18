package ru.studentai.core.common.result

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isSameInstanceAs
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.UnknownException

class DomainResultTest {

    @Test
    fun `Success has value and isSuccess`() {
        val r: DomainResult<Int> = DomainResult.Success(42)

        assertThat(r.isSuccess).isTrue()
        assertThat(r.getOrNull()).isEqualTo(42)
        assertThat(r.errorOrNull()).isNull()
    }

    @Test
    fun `Failure has error and isFailure`() {
        val err = NetworkException.Timeout()
        val r: DomainResult<Int> = DomainResult.Failure(err)

        assertThat(r.isFailure).isTrue()
        assertThat(r.getOrNull()).isNull()
        assertThat(r.errorOrNull()).isSameInstanceAs(err)
    }

    @Test
    fun `map transforms success value`() {
        val r = DomainResult.Success(10).map { it * 2 }
        assertThat(r.getOrNull()).isEqualTo(20)
    }

    @Test
    fun `map keeps failure untouched`() {
        val err = NetworkException.NoConnection()
        val r: DomainResult<Int> = DomainResult.Failure(err)
        val mapped = r.map { it + 1 }
        assertThat(mapped.errorOrNull()).isSameInstanceAs(err)
    }

    @Test
    fun `flatMap chains successful operations`() {
        val r = DomainResult.Success(2).flatMap { DomainResult.Success(it * 3) }
        assertThat(r.getOrNull()).isEqualTo(6)
    }

    @Test
    fun `flatMap propagates first failure`() {
        val err = NetworkException.Timeout()
        val r: DomainResult<Int> = DomainResult.Success(2)
            .flatMap { DomainResult.Failure(err) }
            .flatMap { DomainResult.Success<Int>(99) }
        assertThat(r.errorOrNull()).isSameInstanceAs(err)
    }

    @Test
    fun `mapError transforms failure`() {
        val original = NetworkException.Timeout()
        val replacement = UnknownException("oops")
        val r: DomainResult<Int> = DomainResult.Failure(original).mapError { replacement }
        assertThat(r.errorOrNull()).isSameInstanceAs(replacement)
    }

    @Test
    fun `getOrElse returns value on success`() {
        val v = DomainResult.Success(7).getOrElse { -1 }
        assertThat(v).isEqualTo(7)
    }

    @Test
    fun `getOrElse returns fallback on failure`() {
        val v: Int = DomainResult.Failure(NetworkException.Timeout()).getOrElse { -1 }
        assertThat(v).isEqualTo(-1)
    }

    @Test
    fun `getOrThrow throws on failure`() {
        val err = NetworkException.NoConnection()
        val ex = assertThrows<AppException> {
            (DomainResult.Failure(err) as DomainResult<Int>).getOrThrow()
        }
        assertThat(ex).isSameInstanceAs(err)
    }

    @Test
    fun `fold runs onSuccess for success`() {
        val r = DomainResult.Success(3).fold(
            onSuccess = { "v=$it" },
            onFailure = { "err" },
        )
        assertThat(r).isEqualTo("v=3")
    }

    @Test
    fun `fold runs onFailure for failure`() {
        val r: String = (DomainResult.Failure(NetworkException.Timeout()) as DomainResult<Int>)
            .fold(onSuccess = { "ok" }, onFailure = { "err:${it::class.simpleName}" })
        assertThat(r).isEqualTo("err:Timeout")
    }

    @Test
    fun `onSuccess side-effect runs only for Success`() {
        var captured = 0
        DomainResult.Success(5).onSuccess { captured = it }
        DomainResult.Failure(NetworkException.Timeout()).onSuccess { captured = -1 }
        assertThat(captured).isEqualTo(5)
    }

    @Test
    fun `onFailure side-effect runs only for Failure`() {
        var captured: AppException? = null
        val err = NetworkException.Timeout()
        DomainResult.Success(5).onFailure { captured = it }
        DomainResult.Failure(err).onFailure { captured = it }
        assertThat(captured!!).isSameInstanceAs(err)
    }

    @Test
    fun `recover replaces failure with value`() {
        val r: DomainResult<Int> = DomainResult.Failure(NetworkException.Timeout()).recover { 0 }
        assertThat(r).isInstanceOf(DomainResult.Success::class)
        assertThat(r.getOrNull()).isEqualTo(0)
    }

    @Test
    fun `recoverWith replaces failure with another DomainResult`() {
        val r: DomainResult<Int> = DomainResult.Failure(NetworkException.Timeout())
            .recoverWith { DomainResult.Success(123) }
        assertThat(r.getOrNull()).isEqualTo(123)
    }

    @Test
    fun `combine merges two successes`() {
        val a = DomainResult.Success(2)
        val b = DomainResult.Success("x")
        val combined = a.combine(b) { ai, bi -> "$ai-$bi" }
        assertThat(combined.getOrNull()).isEqualTo("2-x")
    }

    @Test
    fun `combine returns first failure`() {
        val err = NetworkException.Timeout()
        val a: DomainResult<Int> = DomainResult.Failure(err)
        val b = DomainResult.Success("x")
        val combined = a.combine(b) { ai, bi -> "$ai-$bi" }
        assertThat(combined.errorOrNull()).isSameInstanceAs(err)
    }

    @Test
    fun `asSuccess and asFailure helpers`() {
        assertThat(42.asSuccess().getOrNull()).isEqualTo(42)
        val err = NetworkException.Timeout()
        assertThat(err.asFailure().errorOrNull()).isSameInstanceAs(err)
    }
}
