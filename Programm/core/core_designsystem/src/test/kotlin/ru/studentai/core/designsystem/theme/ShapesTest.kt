package ru.studentai.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class ShapesTest {

    @Test
    fun `shapes follow Material 3 corner radii spec`() {
        assertThat(StudentAiShapes.extraSmall).isEqualTo(RoundedCornerShape(4.dp))
        assertThat(StudentAiShapes.small).isEqualTo(RoundedCornerShape(8.dp))
        assertThat(StudentAiShapes.medium).isEqualTo(RoundedCornerShape(12.dp))
        assertThat(StudentAiShapes.large).isEqualTo(RoundedCornerShape(16.dp))
        assertThat(StudentAiShapes.extraLarge).isEqualTo(RoundedCornerShape(28.dp))
    }
}
