package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.domain.model.Subject

/**
 * Чипсы фильтра по предмету (ТЗ §4.2.3).
 *
 * Первый чип — «Сбросить фильтр», далее — список предметов.
 */
@Composable
internal fun FilterChips(
    subjects: List<Subject>,
    selectedSubjectId: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = StudentAiTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.xs),
    ) {
        item(key = "all") {
            FilterChip(
                selected = selectedSubjectId == null,
                onClick = { onSelected(null) },
                label = { Text(stringResource(R.string.feature_schedule_action_clear_filter)) },
            )
        }
        items(items = subjects, key = { it.id }) { subject ->
            FilterChip(
                selected = selectedSubjectId == subject.id,
                onClick = { onSelected(subject.id) },
                label = { Text(subject.name) },
            )
        }
    }
}
