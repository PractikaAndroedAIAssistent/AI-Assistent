package ru.studentai.core.ui.mvi

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.result.DomainResult

class ContentStateTest {

    @Test
    fun `from Success with non-empty data returns Success`() {
        val r = ContentState.from(DomainResult.Success(listOf(1, 2)))
        assertThat(r).isInstanceOf(ContentState.Success::class)
        assertThat((r as ContentState.Success).data).isEqualTo(listOf(1, 2))
    }

    @Test
    fun `from Success with empty predicate returns Empty`() {
        val r = ContentState.from(
            result = DomainResult.Success(emptyList<Int>()),
            isEmpty = { it.isEmpty() },
        )
        assertThat(r).isEqualTo(ContentState.Empty)
    }

    @Test
    fun `from Failure returns Error wrapping AppException`() {
        val err = NetworkException.Timeout()
        val r = ContentState.from(DomainResult.Failure(err))
        assertThat(r).isInstanceOf(ContentState.Error::class)
        assertThat((r as ContentState.Error).error).isEqualTo(err)
    }

    @Test
    fun `isLoadingOrIdle covers Idle and Loading`() {
        assertThat(ContentState.Idle.isLoadingOrIdle).isTrue()
        assertThat(ContentState.Loading.isLoadingOrIdle).isTrue()
    }

    @Test
    fun `isLoadingOrIdle is false for Success Empty Error`() {
        assertThat(ContentState.Success("data").isLoadingOrIdle).isEqualTo(false)
        assertThat(ContentState.Empty.isLoadingOrIdle).isEqualTo(false)
        assertThat(ContentState.Error(NetworkException.Timeout()).isLoadingOrIdle).isEqualTo(false)
    }

    @Test
    fun `dataOrNull returns value for Success`() {
        assertThat(ContentState.Success(42).dataOrNull()).isEqualTo(42)
    }

    @Test
    fun `dataOrNull returns null for non-Success states`() {
        assertThat(ContentState.Idle.dataOrNull()).isNull()
        assertThat(ContentState.Loading.dataOrNull()).isNull()
        assertThat(ContentState.Empty.dataOrNull()).isNull()
        assertThat(ContentState.Error(NetworkException.Timeout()).dataOrNull()).isNull()
    }
}
