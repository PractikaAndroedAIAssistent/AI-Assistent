package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.jupiter.api.Test

class DurationConverterTest {

    private val sut = DurationConverter()

    @Test
    fun `round-trip preserves value`() {
        val original = 45.minutes + 30.seconds
        assertThat(sut.toDuration(sut.fromDuration(original))).isEqualTo(original)
    }

    @Test
    fun `zero duration preserved`() {
        val zero = 0.seconds
        assertThat(sut.toDuration(sut.fromDuration(zero))).isEqualTo(zero)
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromDuration(null)).isNull()
        assertThat(sut.toDuration(null)).isNull()
    }
}
