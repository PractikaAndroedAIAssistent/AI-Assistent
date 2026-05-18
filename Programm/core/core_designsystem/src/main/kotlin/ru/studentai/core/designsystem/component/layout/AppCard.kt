package ru.studentai.core.designsystem.component.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Стиль карточки.
 */
@Immutable
public enum class AppCardStyle { Filled, Outlined, Elevated }

/**
 * Унифицированная карточка дизайн-системы.
 *
 * @param style          вариант оформления
 * @param onClick        опциональный клик; если задан — карточка становится тапабельной
 * @param contentPadding внутренний padding контента
 */
@Composable
public fun AppCard(
    modifier: Modifier = Modifier,
    style: AppCardStyle = AppCardStyle.Filled,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(StudentAiTheme.spacing.md),
    content: @Composable () -> Unit,
) {
    val shape = MaterialTheme.shapes.large

    when (style) {
        AppCardStyle.Filled -> {
            if (onClick != null) {
                Card(
                    onClick = onClick,
                    modifier = modifier,
                    shape = shape,
                ) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            } else {
                Card(modifier = modifier, shape = shape) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            }
        }
        AppCardStyle.Outlined -> {
            if (onClick != null) {
                OutlinedCard(
                    onClick = onClick,
                    modifier = modifier,
                    shape = shape,
                ) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            } else {
                OutlinedCard(modifier = modifier, shape = shape) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            }
        }
        AppCardStyle.Elevated -> {
            if (onClick != null) {
                ElevatedCard(
                    onClick = onClick,
                    modifier = modifier,
                    shape = shape,
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = StudentAiTheme.elevations.level2,
                    ),
                ) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            } else {
                ElevatedCard(
                    modifier = modifier,
                    shape = shape,
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = StudentAiTheme.elevations.level2,
                    ),
                ) {
                    Column(Modifier.padding(contentPadding), content = { content() })
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppCardPreview() = PreviewBox {
    AppCard {
        Text("Лекция: Структуры данных", style = MaterialTheme.typography.titleMedium)
        Text("09:00 – 10:30, ауд. 305", style = MaterialTheme.typography.bodyMedium)
    }
}
