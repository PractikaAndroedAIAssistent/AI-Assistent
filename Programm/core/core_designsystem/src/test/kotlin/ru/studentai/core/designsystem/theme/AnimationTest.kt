package ru.studentai.core.designsystem.theme

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class AnimationTest {

    private val sut = StudentAiAnimation()

    @Test
    fun `default durations follow M3 motion guidelines`() {
        assertThat(sut.durationShortMs).isEqualTo(150)
        assertThat(sut.durationMediumMs).isEqualTo(300)
        assertThat(sut.durationLongMs).isEqualTo(500)
        assertThat(sut.durationExtraLongMs).isEqualTo(700)
    }

    @Test
    fun `durations are strictly increasing`() {
        val durations = listOf(
            sut.durationShortMs,
            sut.durationMediumMs,
            sut.durationLongMs,
            sut.durationExtraLongMs,
        )
        durations.zipWithNext().forEach { (a, b) ->
            assertThat(b > a).isEqualTo(true)
        }
    }
}
