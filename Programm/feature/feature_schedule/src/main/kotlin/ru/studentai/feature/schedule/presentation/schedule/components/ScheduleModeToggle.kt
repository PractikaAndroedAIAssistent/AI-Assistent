package ru.studentai.feature.schedule.presentation.schedule.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.presentation.schedule.ScheduleMode

/**
 * Переключатель режима: День / Неделя.
 */
@Composable
internal fun ScheduleModeToggle(
    mode: ScheduleMode,
    onModeChanged: (ScheduleMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md),
    ) {
        val options = ScheduleMode.values()
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = mode == option,
                onClick = { onModeChanged(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) {
                Text(
                    text = stringResource(
                        when (option) {
                            ScheduleMode.Day -> R.string.feature_schedule_mode_day
                            ScheduleMode.Week -> R.string.feature_schedule_mode_week
                        },
                    ),
                )
            }
        }
    }
}
