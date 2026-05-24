package ru.studentai.feature.tasks.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import ru.studentai.feature.tasks.R
import ru.studentai.feature.tasks.domain.model.TaskPriority

@Composable
@ReadOnlyComposable
public fun TaskPriority.localizedLabel(): String = stringResource(
    when (this) {
        TaskPriority.Low -> R.string.feature_tasks_priority_low
        TaskPriority.Normal -> R.string.feature_tasks_priority_normal
        TaskPriority.High -> R.string.feature_tasks_priority_high
        TaskPriority.Critical -> R.string.feature_tasks_priority_critical
    },
)
