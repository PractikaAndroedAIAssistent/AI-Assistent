package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.domain.model.ScheduleItem

/**
 * Список занятий за неделю, сгруппированный по дням.
 */
@Composable
internal fun WeekListGroupedByDay(
    items: List<ScheduleItem>,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = StudentAiTheme.spacing.sm),
) {
    val grouped = items.groupBy { it.startAt.date }.toSortedMap()
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
    ) {
        grouped.forEach { (date, lessons) ->
            item(key = "header-${date}") {
                DayHeader(date = date)
            }
            items(items = lessons, key = { it.id }) { item ->
                LessonCard(
                    item = item,
                    onClick = { onItemClick(item.id) },
                    onDelete = { onDelete(item.id) },
                )
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate) {
    Text(
        text = "${stringResource(date.dayOfWeek.fullLabelRes())} · ${formatDate(date)}",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = StudentAiTheme.spacing.md,
            top = StudentAiTheme.spacing.sm,
            bottom = StudentAiTheme.spacing.xs,
        ),
    )
}

private fun formatDate(date: LocalDate): String =
    "${date.dayOfMonth.toString().padStart(2, '0')}.${date.monthNumber.toString().padStart(2, '0')}"

private fun DayOfWeek.fullLabelRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.feature_schedule_day_monday_short
    DayOfWeek.TUESDAY -> R.string.feature_schedule_day_tuesday_short
    DayOfWeek.WEDNESDAY -> R.string.feature_schedule_day_wednesday_short
    DayOfWeek.THURSDAY -> R.string.feature_schedule_day_thursday_short
    DayOfWeek.FRIDAY -> R.string.feature_schedule_day_friday_short
    DayOfWeek.SATURDAY -> R.string.feature_schedule_day_saturday_short
    DayOfWeek.SUNDAY -> R.string.feature_schedule_day_sunday_short
}
