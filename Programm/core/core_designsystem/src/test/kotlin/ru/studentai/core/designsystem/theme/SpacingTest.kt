package ru.studentai.core.designsystem.theme

import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class SpacingTest {

    private val sut = StudentAiSpacing()

    @Test
    fun `default spacing follows 4dp baseline grid`() {
        assertThat(sut.none).isEqualTo(0.dp)
        assertThat(sut.xxs).isEqualTo(2.dp)
        assertThat(sut.xs).isEqualTo(4.dp)
        assertThat(sut.sm).isEqualTo(8.dp)
        assertThat(sut.md).isEqualTo(16.dp)
        assertThat(sut.lg).isEqualTo(24.dp)
        assertThat(sut.xl).isEqualTo(32.dp)
        assertThat(sut.xxl).isEqualTo(48.dp)
        assertThat(sut.xxxl).isEqualTo(64.dp)
    }

    @Test
    fun `screen insets default to 16dp`() {
        assertThat(sut.screenHorizontal).isEqualTo(16.dp)
        assertThat(sut.screenVertical).isEqualTo(16.dp)
    }

    @Test
    fun `minTouchTarget matches Material accessibility guideline`() {
        assertThat(sut.minTouchTarget).isEqualTo(48.dp)
    }

    @Test
    fun `data class supports copy override`() {
        val custom = sut.copy(md = 20.dp)
        assertThat(custom.md).isEqualTo(20.dp)
        assertThat(custom.sm).isEqualTo(8.dp)
    }
}
