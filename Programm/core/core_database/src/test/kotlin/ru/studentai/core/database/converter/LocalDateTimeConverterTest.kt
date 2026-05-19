package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test

class LocalDateTimeConverterTest {

    private val sut = LocalDateTimeConverter()

    @Test
    fun `round-trip preserves value`() {
        val original = LocalDateTime.parse("2026-05-18T09:30:00")
        assertThat(sut.toLocalDateTime(sut.fromLocalDateTime(original))).isEqualTo(original)
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromLocalDateTime(null)).isNull()
        assertThat(sut.toLocalDateTime(null)).isNull()
    }
}
