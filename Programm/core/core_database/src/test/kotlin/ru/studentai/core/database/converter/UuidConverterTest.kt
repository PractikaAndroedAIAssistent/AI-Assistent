package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import java.util.UUID
import org.junit.jupiter.api.Test

class UuidConverterTest {

    private val sut = UuidConverter()

    @Test
    fun `round-trip preserves value`() {
        val original = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        assertThat(sut.toUuid(sut.fromUuid(original))).isEqualTo(original)
    }

    @Test
    fun `random uuid round-trips`() {
        val random = UUID.randomUUID()
        assertThat(sut.toUuid(sut.fromUuid(random))).isEqualTo(random)
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromUuid(null)).isNull()
        assertThat(sut.toUuid(null)).isNull()
    }
}
