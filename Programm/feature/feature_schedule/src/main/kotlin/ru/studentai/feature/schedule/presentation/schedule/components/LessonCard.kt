package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.presentation.common.localizedLabel

/**
 * Карточка одного занятия (ТЗ §4.2.3: время / предмет / тип / аудитория / преподаватель).
 */
@Composable
internal fun LessonCard(
    item: ScheduleItem,
    onClick: () -> Unit,
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
            TimeColumn(start = item.startAt, end = item.endAt)
            Spacer(Modifier.width(StudentAiTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.subjectName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
                LessonTypeChip(
                    lessonType = item.lessonType,
                    customLabel = item.customTypeLabel,
                )
                if (!item.room.isNullOrBlank() || !item.teacher.isNullOrBlank()) {
                    Spacer(Modifier.height(StudentAiTheme.spacing.xs))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.md),
                    ) {
                        if (!item.room.isNullOrBlank()) {
                            DetailLabel("Аудитория", item.room)
                        }
                        if (!item.teacher.isNullOrBlank()) {
                            DetailLabel("Преподаватель", item.teacher)
                        }
                    }
                }
                if (!item.note.isNullOrBlank()) {
                    Spacer(Modifier.height(StudentAiTheme.spacing.xs))
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = StudentAiIcons.Delete,
                    contentDescription = stringResource(R.string.feature_schedule_action_delete),
                )
            }
        }
    }
}

@Composable
private fun TimeColumn(start: LocalDateTime, end: LocalDateTime) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.width(60.dp)) {
        Text(
            text = formatTime(start),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = formatTime(end),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LessonTypeChip(lessonType: LessonType, customLabel: String?) {
    val label = customLabel?.takeIf { lessonType == LessonType.Other && it.isNotBlank() }
        ?: lessonType.localizedLabel()
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = Modifier.size(width = 110.dp, height = 32.dp),
    )
}

@Composable
private fun DetailLabel(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun formatTime(time: LocalDateTime): String =
    "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
