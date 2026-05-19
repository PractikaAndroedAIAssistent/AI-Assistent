package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test

class LocalDateConverterTest {

    private val sut = LocalDateConverter()

    @Test
    fun `round-trip preserves date`() {
        val original = LocalDate.parse("2026-05-18")
        assertThat(sut.toLocalDate(sut.fromLocalDate(original))).isEqualTo(original)
    }

    @Test
    fun `serialized form is ISO-8601`() {
        val date = LocalDate(2026, 5, 18)
        assertThat(sut.fromLocalDate(date)).isEqualTo("2026-05-18")
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromLocalDate(null)).isNull()
        assertThat(sut.toLocalDate(null)).isNull()
    }
}
