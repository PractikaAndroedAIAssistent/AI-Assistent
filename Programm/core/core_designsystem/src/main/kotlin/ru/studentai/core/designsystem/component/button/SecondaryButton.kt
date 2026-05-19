package ru.studentai.core.designsystem.component.button

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews

/**
 * Вторичная кнопка (outlined). Используется для второстепенных действий
 * рядом с [PrimaryButton] либо для деструктивных подтверждений.
 */
@Composable
public fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = StudentAiButtonDefaults.MinHeight)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            },
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.small,
        contentPadding = StudentAiButtonDefaults.ContentPadding,
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }
}

@ThemePreviews
@Composable
private fun SecondaryButtonPreview() = PreviewBox {
    SecondaryButton(text = "Отмена", onClick = {})
}
