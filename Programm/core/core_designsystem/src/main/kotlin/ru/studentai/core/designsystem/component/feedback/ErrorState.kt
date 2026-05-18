package ru.studentai.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.SecondaryButton
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Полноэкранное состояние ошибки.
 *
 * Не привязан к конкретному типу исключения — текст ошибки приходит сверху
 * (`message`). Маппинг `AppException → message` должен происходить на уровне
 * feature_*'a через `ErrorMessageResolver` (появится в `core_ui`).
 *
 * @param title         заголовок ошибки (короткий, например "Ошибка")
 * @param message       поясняющий текст ошибки
 * @param onRetry       обработчик повтора (если null — кнопка не показывается)
 * @param retryLabel    подпись кнопки повтора
 * @param secondaryActionLabel  опциональная вторая кнопка (например, «Перейти в офлайн»)
 */
@Composable
public fun ErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = StudentAiIcons.ErrorOutline,
    onRetry: (() -> Unit)? = null,
    retryLabel: String = "Повторить",
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error,
            )
            Text(
                text = title,
                modifier = Modifier.padding(top = StudentAiTheme.spacing.md),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                modifier = Modifier.padding(top = StudentAiTheme.spacing.xs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                PrimaryButton(
                    text = retryLabel,
                    onClick = onRetry,
                    modifier = Modifier.padding(top = StudentAiTheme.spacing.lg),
                    leadingIcon = StudentAiIcons.Refresh,
                )
            }
            if (secondaryActionLabel != null && onSecondaryAction != null) {
                SecondaryButton(
                    text = secondaryActionLabel,
                    onClick = onSecondaryAction,
                    modifier = Modifier.padding(top = StudentAiTheme.spacing.sm),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ErrorStatePreview() = PreviewBox {
    ErrorState(
        title = "Нет соединения",
        message = "Проверьте подключение к интернету и повторите попытку.",
        onRetry = {},
    )
}
