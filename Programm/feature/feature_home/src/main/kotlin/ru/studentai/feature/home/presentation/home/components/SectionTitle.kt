package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Стандартный заголовок секции главного экрана:
 *  • `titleMedium` стиль, на surface;
 *  • справа — опциональный action (Composable, например, TertiaryButton «Все» → navigate).
 */
@Composable
internal fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = StudentAiTheme.spacing.md,
                end = StudentAiTheme.spacing.sm,
                top = StudentAiTheme.spacing.md,
                bottom = StudentAiTheme.spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
        if (trailing != null) trailing()
    }
}
