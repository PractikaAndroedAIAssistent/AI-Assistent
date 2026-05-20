package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppHorizontalDivider
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.DeadlinePriority

/**
 * Карточка «Дедлайны на неделю» (ТЗ §4.2.4).
 *
 * Особенности:
 *  • просроченные элементы помечены красным цветом и зачёркиванием времени;
 *  • цветная точка-«индикатор» приоритета слева от заголовка.
 */
@Composable
internal fun WeekDeadlinesCard(
    deadlines: List<DeadlineItem>,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_week_deadlines))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable -> PlaceholderText(
                    stringResource(R.string.feature_home_week_deadlines_unavailable),
                )
                deadlines.isEmpty() -> PlaceholderText(
                    stringResource(R.string.feature_home_week_deadlines_empty),
                )
                else -> DeadlinesList(deadlines)
            }
        }
    }
}

@Composable
private fun DeadlinesList(deadlines: List<DeadlineItem>) {
    deadlines.forEachIndexed { index, item ->
        if (index != 0) AppHorizontalDivider(Modifier.padding(vertical = StudentAiTheme.spacing.xs))
        DeadlineRow(item)
    }
}

@Composable
private fun DeadlineRow(item: DeadlineItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PriorityDot(priority = item.priority)
        Spacer(Modifier.width(StudentAiTheme.spacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.subject,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
        }
        Spacer(Modifier.width(StudentAiTheme.spacing.sm))
        Text(
            text = formatDeadlineDate(item.dueAt),
            style = MaterialTheme.typography.labelMedium,
            color = if (item.isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (item.isOverdue) TextDecoration.LineThrough else TextDecoration.None,
        )
    }
}

@Composable
private fun PriorityDot(priority: DeadlinePriority) {
    val color: Color = when (priority) {
        DeadlinePriority.Low -> MaterialTheme.colorScheme.outline
        DeadlinePriority.Normal -> MaterialTheme.colorScheme.secondary
        DeadlinePriority.High -> MaterialTheme.colorScheme.tertiary
        DeadlinePriority.Critical -> MaterialTheme.colorScheme.error
    }
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape),
    )
}

@Composable
private fun PlaceholderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun formatDeadlineDate(at: LocalDateTime): String {
    val day = at.dayOfMonth.toString().padStart(2, '0')
    val month = at.monthNumber.toString().padStart(2, '0')
    val hour = at.hour.toString().padStart(2, '0')
    val minute = at.minute.toString().padStart(2, '0')
    return "$day.$month $hour:$minute"
}
