package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.R

/**
 * Полоса недели: 7 дней с подсветкой выбранного. Используется в режиме «День»
 * — позволяет быстро переключаться между днями недели.
 */
@Composable
internal fun WeekStripPicker(
    anchor: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monday = remember(anchor) { anchor.toMonday() }
    val days = remember(monday) { (0..6).map { monday.plus(DatePeriod(days = it)) } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md, vertical = StudentAiTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEach { date ->
            DayCell(
                date = date,
                isSelected = date == anchor,
                onClick = { onDateSelected(date) },
            )
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor: Color = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    val contentColor: Color = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(StudentAiTheme.spacing.xxs),
    ) {
        Text(
            text = stringResource(date.dayOfWeek.shortLabelRes()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(
            modifier = Modifier
                .size(36.dp)
                .background(containerColor, CircleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
            )
        }
    }
}

private fun LocalDate.toMonday(): LocalDate {
    val offset = (dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
    return if (offset == 0) this else this.minus(DatePeriod(days = offset))
}

private fun DayOfWeek.shortLabelRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.feature_schedule_day_monday_short
    DayOfWeek.TUESDAY -> R.string.feature_schedule_day_tuesday_short
    DayOfWeek.WEDNESDAY -> R.string.feature_schedule_day_wednesday_short
    DayOfWeek.THURSDAY -> R.string.feature_schedule_day_thursday_short
    DayOfWeek.FRIDAY -> R.string.feature_schedule_day_friday_short
    DayOfWeek.SATURDAY -> R.string.feature_schedule_day_saturday_short
    DayOfWeek.SUNDAY -> R.string.feature_schedule_day_sunday_short
}
