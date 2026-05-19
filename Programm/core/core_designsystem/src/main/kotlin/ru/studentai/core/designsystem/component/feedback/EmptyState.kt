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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Пустое состояние списка/экрана с подсказкой о следующем действии.
 *
 * Соответствует ТЗ §4.1.6: «Пустые состояния экранов должны содержать подсказку о следующем действии».
 *
 * @param title           заголовок (короткий)
 * @param description     поясняющий текст (опционально)
 * @param icon            иконка, поясняющая контекст
 * @param actionLabel     если задан и есть [onAction], отображается кнопка
 * @param onAction        обработчик клика по кнопке действия
 */
@Composable
public fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector = StudentAiIcons.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    Box(
        modifier = modifier.fillMaxSize().padding(contentPadding),
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title,
                modifier = Modifier.padding(top = StudentAiTheme.spacing.md),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            if (description != null) {
                Text(
                    text = description,
                    modifier = Modifier.padding(top = StudentAiTheme.spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (actionLabel != null && onAction != null) {
                PrimaryButton(
                    text = actionLabel,
                    onClick = onAction,
                    modifier = Modifier.padding(top = StudentAiTheme.spacing.lg),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun EmptyStatePreview() = PreviewBox {
    EmptyState(
        title = "Нет конспектов",
        description = "Создайте первый конспект, чтобы он отображался здесь.",
        icon = StudentAiIcons.Notes,
        actionLabel = "Создать",
        onAction = {},
    )
}
