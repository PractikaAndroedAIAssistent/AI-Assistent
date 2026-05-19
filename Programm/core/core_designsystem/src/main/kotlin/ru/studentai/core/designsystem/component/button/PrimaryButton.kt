package ru.studentai.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews

/**
 * Основная кнопка экранов (filled M3).
 *
 * @param text          подпись кнопки
 * @param onClick       обработчик клика; не вызывается при `enabled = false` или `loading = true`
 * @param modifier      внешний модификатор
 * @param enabled       доступность кнопки
 * @param loading       если `true` — показывается индикатор вместо текста, кнопка disabled
 * @param leadingIcon   иконка слева от текста (опционально)
 * @param trailingIcon  иконка справа от текста (опционально)
 * @param contentDescription для accessibility — особенно важно для icon-only-вариантов
 */
@Composable
public fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null,
) {
    Button(
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

@Composable
internal fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(StudentAiButtonDefaults.LoadingIndicatorSize),
            strokeWidth = StudentAiButtonDefaults.LoadingIndicatorStrokeWidth,
            color = LocalContentColor.current,
        )
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(StudentAiButtonDefaults.IconSize),
            )
            Spacer(Modifier.width(StudentAiButtonDefaults.IconPadding))
        }
        Text(text = text)
        if (trailingIcon != null) {
            Spacer(Modifier.width(StudentAiButtonDefaults.IconPadding))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(StudentAiButtonDefaults.IconSize),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PrimaryButtonPreview() = PreviewBox {
    PrimaryButton(text = "Войти", onClick = {})
}

@ThemePreviews
@Composable
private fun PrimaryButtonLoadingPreview() = PreviewBox {
    PrimaryButton(text = "Войти", onClick = {}, loading = true)
}

@ThemePreviews
@Composable
private fun PrimaryButtonDisabledPreview() = PreviewBox {
    PrimaryButton(text = "Войти", onClick = {}, enabled = false)
}
