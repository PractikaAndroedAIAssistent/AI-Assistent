package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.domain.model.ScheduleItem

/**
 * Простой список занятий за один день.
 */
@Composable
internal fun DayList(
    items: List<ScheduleItem>,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = StudentAiTheme.spacing.sm),
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
    ) {
        items(items = items, key = { it.id }) { item ->
            LessonCard(
                item = item,
                onClick = { onItemClick(item.id) },
                onDelete = { onDelete(item.id) },
            )
        }
    }
}
