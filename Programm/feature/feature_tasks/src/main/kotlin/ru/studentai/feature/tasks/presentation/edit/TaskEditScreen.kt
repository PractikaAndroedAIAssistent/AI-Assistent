package ru.studentai.feature.tasks.presentation.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.SecondaryButton
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.input.AppTextField
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppTopBar
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.feature.tasks.R
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.presentation.common.localizedLabel

@Composable
public fun TaskEditScreen(
    itemId: String?,
    onClose: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) { viewModel.dispatch(TaskEditEvent.Init(itemId)) }
    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            TaskEditEffect.Saved, TaskEditEffect.Cancelled -> onClose()
            is TaskEditEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    val titleRes = if (itemId == null) R.string.feature_tasks_edit_new
    else R.string.feature_tasks_edit_edit

    ScreenScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(titleRes),
                onNavigateBack = { viewModel.dispatch(TaskEditEvent.CancelClicked) },
            )
        },
        snackbarHostState = snackbar,
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            return@ScreenScaffold
        }
        Form(
            state = state,
            onEvent = viewModel::dispatch,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = StudentAiTheme.spacing.md),
        )
    }
}

@Composable
private fun Form(
    state: TaskEditState,
    onEvent: (TaskEditEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))

        AppTextField(
            value = state.form.title,
            onValueChange = { onEvent(TaskEditEvent.TitleChanged(it)) },
            label = stringResource(R.string.feature_tasks_field_title),
            errorMessage = state.titleError,
        )
        AppTextField(
            value = state.form.subjectName,
            onValueChange = { onEvent(TaskEditEvent.SubjectChanged(it)) },
            label = stringResource(R.string.feature_tasks_field_subject),
        )
        AppTextField(
            value = state.form.description,
            onValueChange = { onEvent(TaskEditEvent.DescriptionChanged(it)) },
            label = stringResource(R.string.feature_tasks_field_description),
            singleLine = false,
            maxLines = 4,
        )

        AppTextField(
            value = state.form.dueDate?.toString().orEmpty(),
            onValueChange = { input ->
                runCatching { LocalDate.parse(input) }.onSuccess {
                    onEvent(TaskEditEvent.DateChanged(it))
                }
            },
            label = stringResource(R.string.feature_tasks_field_due_date),
            placeholder = "YYYY-MM-DD",
            supportingText = "Формат: 2026-05-25",
        )
        AppTextField(
            value = state.form.dueTime?.toShortDisplay().orEmpty(),
            onValueChange = { input -> parseTime(input)?.let { onEvent(TaskEditEvent.TimeChanged(it)) } },
            label = stringResource(R.string.feature_tasks_field_due_time),
            placeholder = "HH:MM",
            errorMessage = state.dueError,
        )

        PriorityDropdown(
            selected = state.form.priority,
            onSelect = { onEvent(TaskEditEvent.PriorityChanged(it)) },
        )

        if (state.form.role == TaskRole.Teacher) {
            AppTextField(
                value = state.form.groupName,
                onValueChange = { onEvent(TaskEditEvent.GroupChanged(it)) },
                label = stringResource(R.string.feature_tasks_field_group),
                errorMessage = state.groupError,
            )
        }

        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
        ) {
            SecondaryButton(
                text = stringResource(R.string.feature_tasks_action_cancel),
                onClick = { onEvent(TaskEditEvent.CancelClicked) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = stringResource(R.string.feature_tasks_action_save),
                onClick = { onEvent(TaskEditEvent.SaveClicked) },
                loading = state.isSaving,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
    }
}

@Composable
private fun PriorityDropdown(
    selected: TaskPriority,
    onSelect: (TaskPriority) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        AppTextField(
            value = selected.localizedLabel(),
            onValueChange = {},
            label = stringResource(R.string.feature_tasks_field_priority),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TaskPriority.values().forEach { value ->
                DropdownMenuItem(
                    text = { Text(value.localizedLabel()) },
                    onClick = { onSelect(value); expanded = false },
                )
            }
        }
    }
}

private fun LocalTime.toShortDisplay(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

private fun parseTime(raw: String): LocalTime? {
    val parts = raw.split(':')
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return LocalTime(hour = h, minute = m)
}
