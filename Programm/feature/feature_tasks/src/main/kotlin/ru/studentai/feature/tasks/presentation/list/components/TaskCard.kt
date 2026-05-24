package ru.studentai.feature.tasks.presentation.list.components

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.tasks.R
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority

/**
 * Карточка задачи. Поддерживает оба варианта [StudyTask] — иконку группы (для преподавателя)
 * и подсветку приоритета.
 */
@Composable
internal fun TaskCard(
    task: StudyTask,
    isOverdue: Boolean,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md),
        style = AppCardStyle.Outlined,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleComplete() })

            Spacer(Modifier.width(StudentAiTheme.spacing.xs))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityDot(priority = task.priority)
                    Spacer(Modifier.width(StudentAiTheme.spacing.xs))
                    Text(
                        text = task.subjectName ?: "—",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (task is StudyTask.TeacherAssignment) {
                        Spacer(Modifier.width(StudentAiTheme.spacing.xs))
                        Text(
                            text = "· ${task.groupName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                val description = task.description
                if (!description.isNullOrBlank()) {
                    Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
                Spacer(Modifier.height(StudentAiTheme.spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = StudentAiIcons.Reminder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isOverdue) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(StudentAiTheme.spacing.xxs))
                    Text(
                        text = formatDateTime(task.dueAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isOverdue) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                        else TextDecoration.None,
                    )
                    if (isOverdue) {
                        Spacer(Modifier.width(StudentAiTheme.spacing.xs))
                        Text(
                            text = stringResource(R.string.feature_tasks_overdue_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = StudentAiIcons.Delete,
                    contentDescription = stringResource(R.string.feature_tasks_action_delete),
                )
            }
        }
    }
}

@Composable
private fun PriorityDot(priority: TaskPriority) {
    val color: Color = when (priority) {
        TaskPriority.Low -> MaterialTheme.colorScheme.outline
        TaskPriority.Normal -> MaterialTheme.colorScheme.secondary
        TaskPriority.High -> MaterialTheme.colorScheme.tertiary
        TaskPriority.Critical -> MaterialTheme.colorScheme.error
    }
    Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
}

private fun formatDateTime(at: LocalDateTime): String =
    "${at.dayOfMonth.toString().padStart(2, '0')}.${at.monthNumber.toString().padStart(2, '0')} " +
        "${at.hour.toString().padStart(2, '0')}:${at.minute.toString().padStart(2, '0')}"
