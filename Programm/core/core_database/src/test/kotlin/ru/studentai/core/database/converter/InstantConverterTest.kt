package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

class InstantConverterTest {

    private val sut = InstantConverter()

    @Test
    fun `round-trip preserves value`() {
        val original = Instant.parse("2026-05-18T10:00:00Z")
        val stored = sut.fromInstant(original)
        val restored = sut.toInstant(stored)
        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `epoch zero is preserved`() {
        val original = Instant.fromEpochMilliseconds(0L)
        assertThat(sut.toInstant(sut.fromInstant(original))).isEqualTo(original)
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromInstant(null)).isNull()
        assertThat(sut.toInstant(null)).isNull()
    }
}
