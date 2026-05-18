package ru.studentai.core.designsystem.component.button

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews

/**
 * Текстовая (третичная) кнопка. Inline-действия в плотных контекстах:
 * «Забыли пароль?», «Подробнее», «Пропустить».
 */
@Composable
public fun TertiaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = StudentAiButtonDefaults.MinHeight)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            },
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.small,
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
private fun TertiaryButtonPreview() = PreviewBox {
    TertiaryButton(text = "Забыли пароль?", onClick = {})
}
