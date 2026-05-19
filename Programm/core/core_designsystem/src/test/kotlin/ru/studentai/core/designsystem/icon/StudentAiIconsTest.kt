package ru.studentai.core.designsystem.icon

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test

class StudentAiIconsTest {

    @Test
    fun `bottom nav role icons are distinct`() {
        val navIcons = listOf(
            StudentAiIcons.Home,
            StudentAiIcons.Schedule,
            StudentAiIcons.Notes,
            StudentAiIcons.Materials,
            StudentAiIcons.Ai,
            StudentAiIcons.Profile,
        )
        // Все иконки нижней навигации должны быть разными — иначе пользователь не различит разделы.
        for (i in navIcons.indices) {
            for (j in i + 1 until navIcons.size) {
                assertThat(navIcons[i]).isNotEqualTo(navIcons[j])
            }
        }
    }

    @Test
    fun `visibility on and off icons differ`() {
        assertThat(StudentAiIcons.VisibilityOn).isNotEqualTo(StudentAiIcons.VisibilityOff)
    }
}
