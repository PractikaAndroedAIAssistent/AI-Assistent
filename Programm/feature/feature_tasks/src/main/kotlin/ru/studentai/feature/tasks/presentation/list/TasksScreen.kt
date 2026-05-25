package ru.studentai.feature.tasks.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.designsystem.component.feedback.EmptyState
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppCenterAlignedTopBar
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.tasks.R
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.model.TaskSort
import ru.studentai.feature.tasks.presentation.list.components.TaskCard

/**
 * Экран дедлайнов/задач (ТЗ §4.2.4).
 */
@Composable
public fun TasksScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: TasksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            TasksEffect.NavigateToAdd -> onNavigateToAdd()
            is TasksEffect.NavigateToEdit -> onNavigateToEdit(effect.id)
            is TasksEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    val titleRes = when (state.role) {
        TaskRole.Student -> R.string.feature_tasks_title_student
        TaskRole.Teacher -> R.string.feature_tasks_title_teacher
    }

    ScreenScaffold(
        topBar = {
            AppCenterAlignedTopBar(
                title = stringResource(titleRes),
                actions = {
                    SortMenu(
                        sort = state.sort,
                        onSelect = { viewModel.dispatch(TasksEvent.SortChanged(it)) },
                    )
                },
            )
        },
        snackbarHostState = snackbar,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.dispatch(TasksEvent.AddClicked) },
                icon = { Icon(StudentAiIcons.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.feature_tasks_action_add)) },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            PresetSegmentedRow(
                preset = state.preset,
                onSelect = { viewModel.dispatch(TasksEvent.FilterPresetChanged(it)) },
            )
            SubjectChips(
                subjects = state.subjects,
                selected = state.subjectFilter,
                onSelect = { viewModel.dispatch(TasksEvent.SubjectFilterChanged(it)) },
            )

            val nowLocal = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
            when (val s = state.items) {
                ContentState.Idle, ContentState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                ContentState.Empty -> EmptyState(
                    title = stringResource(
                        when (state.preset) {
                            TaskListFilterPreset.Overdue -> R.string.feature_tasks_empty_overdue
                            TaskListFilterPreset.Completed -> R.string.feature_tasks_empty_completed
                            else -> R.string.feature_tasks_empty
                        },
                    ),
                    icon = StudentAiIcons.Reminder,
                    actionLabel = stringResource(R.string.feature_tasks_empty_action_add),
                    onAction = { viewModel.dispatch(TasksEvent.AddClicked) },
                    modifier = Modifier.fillMaxSize(),
                )
                is ContentState.Error -> ErrorState(
                    title = stringResource(titleRes),
                    message = s.error.message ?: "—",
                    onRetry = { viewModel.dispatch(TasksEvent.RetryClicked) },
                    modifier = Modifier.fillMaxSize(),
                )
                is ContentState.Success -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = StudentAiTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
                ) {
                    items(items = s.data, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            isOverdue = !task.isCompleted && task.dueAt < nowLocal,
                            onClick = { viewModel.dispatch(TasksEvent.TaskClicked(task.id)) },
                            onToggleComplete = { viewModel.dispatch(TasksEvent.ToggleCompletion(task.id)) },
                            onDelete = { viewModel.dispatch(TasksEvent.Delete(task.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SortMenu(sort: TaskSort, onSelect: (TaskSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(StudentAiIcons.Filter, contentDescription = stringResource(R.string.feature_tasks_sort))
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        listOf(
            TaskSort.ByDate to R.string.feature_tasks_sort_by_date,
            TaskSort.BySubject to R.string.feature_tasks_sort_by_subject,
            TaskSort.ByPriority to R.string.feature_tasks_sort_by_priority,
        ).forEach { (value, labelRes) ->
            DropdownMenuItem(
                text = { Text(stringResource(labelRes)) },
                onClick = { onSelect(value); expanded = false },
            )
        }
    }
}

@Composable
private fun PresetSegmentedRow(
    preset: TaskListFilterPreset,
    onSelect: (TaskListFilterPreset) -> Unit,
) {
    val options = TaskListFilterPreset.values()
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md),
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = preset == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) {
                Text(
                    text = stringResource(
                        when (option) {
                            TaskListFilterPreset.All -> R.string.feature_tasks_filter_all
                            TaskListFilterPreset.Active -> R.string.feature_tasks_filter_active
                            TaskListFilterPreset.Overdue -> R.string.feature_tasks_filter_overdue
                            TaskListFilterPreset.Completed -> R.string.feature_tasks_filter_completed
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun SubjectChips(
    subjects: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
) {
    if (subjects.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = StudentAiTheme.spacing.md,
            vertical = StudentAiTheme.spacing.xs,
        ),
        horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.xs),
    ) {
        item(key = "all") {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text(stringResource(R.string.feature_tasks_filter_all)) },
            )
        }
        items(items = subjects, key = { it }) { subjectName ->
            FilterChip(
                selected = selected == subjectName,
                onClick = { onSelect(subjectName) },
                label = { Text(subjectName) },
            )
        }
    }
}
