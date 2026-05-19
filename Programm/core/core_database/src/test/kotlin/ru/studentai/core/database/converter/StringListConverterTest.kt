package ru.studentai.core.database.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class StringListConverterTest {

    private val sut = StringListConverter()

    @Test
    fun `round-trip preserves list with simple strings`() {
        val original = listOf("математика", "физика", "химия")
        assertThat(sut.toList(sut.fromList(original))).isEqualTo(original)
    }

    @Test
    fun `round-trip preserves list with commas and quotes inside elements`() {
        val original = listOf("Иван, Петров", "she said \"hi\"", "")
        assertThat(sut.toList(sut.fromList(original))).isEqualTo(original)
    }

    @Test
    fun `empty list serializes and deserializes correctly`() {
        val original = emptyList<String>()
        assertThat(sut.toList(sut.fromList(original))).isEqualTo(original)
    }

    @Test
    fun `null in null out`() {
        assertThat(sut.fromList(null)).isNull()
        assertThat(sut.toList(null)).isNull()
    }
}
