package ru.studentai.core.designsystem.theme

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test

class SemanticColorsTest {

    @Test
    fun `light and dark semantic colors differ for success`() {
        assertThat(LightSemanticColors.success).isNotEqualTo(DarkSemanticColors.success)
        assertThat(LightSemanticColors.warning).isNotEqualTo(DarkSemanticColors.warning)
        assertThat(LightSemanticColors.info).isNotEqualTo(DarkSemanticColors.info)
    }

    @Test
    fun `on-color contrasts container in light scheme`() {
        // sanity: container и onContainer не равны — иначе текст будет нечитаем
        assertThat(LightSemanticColors.successContainer).isNotEqualTo(LightSemanticColors.onSuccessContainer)
        assertThat(LightSemanticColors.warningContainer).isNotEqualTo(LightSemanticColors.onWarningContainer)
        assertThat(LightSemanticColors.infoContainer).isNotEqualTo(LightSemanticColors.onInfoContainer)
    }

    @Test
    fun `on-color contrasts container in dark scheme`() {
        assertThat(DarkSemanticColors.successContainer).isNotEqualTo(DarkSemanticColors.onSuccessContainer)
        assertThat(DarkSemanticColors.warningContainer).isNotEqualTo(DarkSemanticColors.onWarningContainer)
        assertThat(DarkSemanticColors.infoContainer).isNotEqualTo(DarkSemanticColors.onInfoContainer)
    }
}
