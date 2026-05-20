package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.UpcomingLesson

/**
 * Карточка «Ближайшая пара» (ТЗ §4.2.2, §4.2.3).
 *
 * Поведение в зависимости от состояния:
 *  • `isProviderAvailable = false` → плейсхолдер «Расписание появится позже»;
 *  • `lesson == null` → плейсхолдер «Сегодня пар больше нет»;
 *  • lesson != null → детали (время, предмет, тип, аудитория, преподаватель).
 */
@Composable
internal fun UpcomingLessonCard(
    lesson: UpcomingLesson?,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_upcoming_lesson))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable -> PlaceholderText(
                    stringResource(R.string.feature_home_upcoming_lesson_unavailable),
                )
                lesson == null -> PlaceholderText(
                    stringResource(R.string.feature_home_upcoming_lesson_empty),
                )
                else -> LessonContent(lesson)
            }
        }
    }
}

@Composable
private fun LessonContent(lesson: UpcomingLesson) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = StudentAiIcons.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(StudentAiTheme.spacing.sm))
        Text(
            text = formatRange(lesson.startAt, lesson.endAt),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
    Spacer(Modifier.height(StudentAiTheme.spacing.xs))
    Text(
        text = lesson.subject,
        style = MaterialTheme.typography.titleSmall,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
    Text(
        text = lesson.lessonType,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(Modifier.height(StudentAiTheme.spacing.sm))
    Row(
        horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.md),
    ) {
        if (!lesson.room.isNullOrBlank()) {
            DetailLabel(label = "Аудитория", value = lesson.room)
        }
        if (!lesson.teacher.isNullOrBlank()) {
            DetailLabel(label = "Преподаватель", value = lesson.teacher)
        }
    }
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

@Composable
private fun PlaceholderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun formatRange(start: LocalDateTime, end: LocalDateTime): String =
    "${start.hour.pad()}:${start.minute.pad()} – ${end.hour.pad()}:${end.minute.pad()}"

private fun Int.pad(): String = toString().padStart(2, '0')
