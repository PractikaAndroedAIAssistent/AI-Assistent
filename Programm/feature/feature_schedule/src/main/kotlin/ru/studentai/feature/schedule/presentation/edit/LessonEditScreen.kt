package ru.studentai.feature.schedule.presentation.edit

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
import androidx.compose.ui.unit.dp
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
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.presentation.common.localizedLabel

/**
 * Экран создания/редактирования занятия (ТЗ §4.2.3).
 *
 * Форма: предмет, тип (+ кастомная подпись для Other), дата (YYYY-MM-DD),
 * время начала/окончания (HH:MM), аудитория, преподаватель, примечание.
 *
 * Полноценные Material 3 date/time-пикеры подключатся в polish-итерации.
 */
@Composable
public fun LessonEditScreen(
    itemId: String?,
    onClose: () -> Unit,
    viewModel: LessonEditViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) {
        viewModel.dispatch(LessonEditEvent.Init(itemId))
    }
    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            LessonEditEffect.Saved, LessonEditEffect.Cancelled -> onClose()
            is LessonEditEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    val titleRes = if (itemId == null) {
        R.string.feature_schedule_lesson_edit_title_new
    } else {
        R.string.feature_schedule_lesson_edit_title_edit
    }

    ScreenScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(titleRes),
                onNavigateBack = { viewModel.dispatch(LessonEditEvent.CancelClicked) },
            )
        },
        snackbarHostState = snackbar,
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            return@ScreenScaffold
        }
        FormBody(
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
private fun FormBody(
    state: LessonEditState,
    onEvent: (LessonEditEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))

        AppTextField(
            value = state.form.subjectName,
            onValueChange = { onEvent(LessonEditEvent.SubjectChanged(it)) },
            label = stringResource(R.string.feature_schedule_field_subject),
            errorMessage = state.subjectError,
        )

        LessonTypeDropdown(
            selected = state.form.lessonType,
            onSelect = { onEvent(LessonEditEvent.LessonTypeChanged(it)) },
        )
        if (state.form.lessonType == LessonType.Other) {
            AppTextField(
                value = state.form.customTypeLabel,
                onValueChange = { onEvent(LessonEditEvent.CustomTypeLabelChanged(it)) },
                label = stringResource(R.string.feature_schedule_field_lesson_type),
            )
        }

        AppTextField(
            value = state.form.date?.toString().orEmpty(),
            onValueChange = { input ->
                runCatching { LocalDate.parse(input) }.onSuccess {
                    onEvent(LessonEditEvent.DateChanged(it))
                }
            },
            label = stringResource(R.string.feature_schedule_field_date),
            placeholder = "YYYY-MM-DD",
            supportingText = "Формат: 2026-05-20",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm)) {
            AppTextField(
                value = state.form.startTime?.toShortDisplay().orEmpty(),
                onValueChange = { input ->
                    parseTime(input)?.let { onEvent(LessonEditEvent.StartTimeChanged(it)) }
                },
                label = stringResource(R.string.feature_schedule_field_start_time),
                placeholder = "HH:MM",
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                value = state.form.endTime?.toShortDisplay().orEmpty(),
                onValueChange = { input ->
                    parseTime(input)?.let { onEvent(LessonEditEvent.EndTimeChanged(it)) }
                },
                label = stringResource(R.string.feature_schedule_field_end_time),
                placeholder = "HH:MM",
                modifier = Modifier.weight(1f),
                errorMessage = state.timeError,
            )
        }

        AppTextField(
            value = state.form.room,
            onValueChange = { onEvent(LessonEditEvent.RoomChanged(it)) },
            label = stringResource(R.string.feature_schedule_field_room),
        )
        AppTextField(
            value = state.form.teacher,
            onValueChange = { onEvent(LessonEditEvent.TeacherChanged(it)) },
            label = stringResource(R.string.feature_schedule_field_teacher),
        )
        AppTextField(
            value = state.form.note,
            onValueChange = { onEvent(LessonEditEvent.NoteChanged(it)) },
            label = stringResource(R.string.feature_schedule_field_note),
            singleLine = false,
            maxLines = 4,
        )

        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
        ) {
            SecondaryButton(
                text = stringResource(R.string.feature_schedule_action_cancel),
                onClick = { onEvent(LessonEditEvent.CancelClicked) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = stringResource(R.string.feature_schedule_action_save),
                onClick = { onEvent(LessonEditEvent.SaveClicked) },
                loading = state.isSaving,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
    }
}

@Composable
private fun LessonTypeDropdown(
    selected: LessonType,
    onSelect: (LessonType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        AppTextField(
            value = selected.localizedLabel(),
            onValueChange = {},
            label = stringResource(R.string.feature_schedule_field_lesson_type),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LessonType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.localizedLabel()) },
                    onClick = {
                        onSelect(type)
                        expanded = false
                    },
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
