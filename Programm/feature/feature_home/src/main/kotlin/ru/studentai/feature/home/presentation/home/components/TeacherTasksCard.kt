package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.datetime.LocalDateTime
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppHorizontalDivider
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.TeacherTask

/**
 * Карточка «Задачи преподавателя» (ТЗ §4.2.2, §4.2.4).
 */
@Composable
internal fun TeacherTasksCard(
    tasks: List<TeacherTask>,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_teacher_tasks))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable -> Placeholder(stringResource(R.string.feature_home_teacher_tasks_unavailable))
                tasks.isEmpty() -> Placeholder(stringResource(R.string.feature_home_teacher_tasks_empty))
                else -> tasks.forEachIndexed { index, task ->
                    if (index != 0) AppHorizontalDivider(Modifier.padding(vertical = StudentAiTheme.spacing.xs))
                    TaskRow(task)
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: TeacherTask) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            if (!task.relatedSubject.isNullOrBlank()) {
                Text(
                    text = task.relatedSubject,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
            }
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
        }
        if (task.dueAt != null) {
            Spacer(Modifier.width(StudentAiTheme.spacing.sm))
            Text(
                text = formatDateTime(task.dueAt),
                style = MaterialTheme.typography.labelMedium,
                color = if (task.isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.isOverdue) TextDecoration.LineThrough else TextDecoration.None,
            )
        }
    }
}

@Composable
private fun Placeholder(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun formatDateTime(at: LocalDateTime): String =
    "${at.dayOfMonth.toString().padStart(2, '0')}.${at.monthNumber.toString().padStart(2, '0')} " +
        "${at.hour.toString().padStart(2, '0')}:${at.minute.toString().padStart(2, '0')}"
