package ru.studentai.core.designsystem.theme

import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class ElevationsTest {

    private val sut = StudentAiElevations()

    @Test
    fun `default elevations match Material 3 spec`() {
        assertThat(sut.level0).isEqualTo(0.dp)
        assertThat(sut.level1).isEqualTo(1.dp)
        assertThat(sut.level2).isEqualTo(3.dp)
        assertThat(sut.level3).isEqualTo(6.dp)
        assertThat(sut.level4).isEqualTo(8.dp)
        assertThat(sut.level5).isEqualTo(12.dp)
    }

    @Test
    fun `level values are monotonically increasing`() {
        val values = listOf(sut.level0, sut.level1, sut.level2, sut.level3, sut.level4, sut.level5)
        values.zipWithNext().forEach { (a, b) ->
            assertThat(b.value > a.value).isEqualTo(true)
        }
    }
}
