package ru.studentai.feature.schedule.presentation.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.studentai.core.designsystem.component.feedback.EmptyState
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppCenterAlignedTopBar
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.presentation.schedule.components.DayList
import ru.studentai.feature.schedule.presentation.schedule.components.FilterChips
import ru.studentai.feature.schedule.presentation.schedule.components.ScheduleModeToggle
import ru.studentai.feature.schedule.presentation.schedule.components.WeekListGroupedByDay
import ru.studentai.feature.schedule.presentation.schedule.components.WeekStripPicker

/**
 * Экран расписания (ТЗ §4.2.3).
 *
 * Содержит:
 *  • TopBar c действиями: «Сегодня», «Импорт из ЛК вуза»
 *  • Mode toggle (День/Неделя)
 *  • Для режима «День» — WeekStripPicker
 *  • Фильтр по предмету (FilterChips)
 *  • Список занятий
 *  • FAB «Добавить занятие»
 */
@Composable
public fun ScheduleScreen(
    onNavigateToAddLesson: () -> Unit,
    onNavigateToEditLesson: (String) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            ScheduleEffect.NavigateToAddLesson -> onNavigateToAddLesson()
            is ScheduleEffect.NavigateToEditLesson -> onNavigateToEditLesson(effect.id)
            is ScheduleEffect.ShowMessage -> scope.launch { snackbarHost.showSnackbar(effect.message) }
        }
    }

    ScreenScaffold(
        topBar = {
            AppCenterAlignedTopBar(
                title = stringResource(R.string.feature_schedule_title),
                actions = {
                    IconButton(onClick = { viewModel.dispatch(ScheduleEvent.TodayClicked) }) {
                        Icon(
                            imageVector = StudentAiIcons.Calendar,
                            contentDescription = stringResource(R.string.feature_schedule_action_today),
                        )
                    }
                    IconButton(onClick = { viewModel.dispatch(ScheduleEvent.ImportFromUniversityClicked) }) {
                        Icon(
                            imageVector = StudentAiIcons.Upload,
                            contentDescription = stringResource(R.string.feature_schedule_action_import),
                        )
                    }
                },
            )
        },
        snackbarHostState = snackbarHost,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.dispatch(ScheduleEvent.AddLessonClicked) },
                icon = { Icon(StudentAiIcons.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.feature_schedule_action_add)) },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScheduleModeToggle(
                mode = state.mode,
                onModeChanged = { viewModel.dispatch(ScheduleEvent.ModeChanged(it)) },
            )

            val anchor = state.anchorDate
            if (state.mode == ScheduleMode.Day && anchor != null) {
                WeekStripPicker(
                    anchor = anchor,
                    onDateSelected = { viewModel.dispatch(ScheduleEvent.DateSelected(it)) },
                )
            }

            FilterChips(
                subjects = state.subjects,
                selectedSubjectId = state.filter.subjectId,
                onSelected = { viewModel.dispatch(ScheduleEvent.FilterSubjectChanged(it)) },
            )

            when (val s = state.items) {
                ContentState.Idle, ContentState.Loading -> LoadingState(
                    modifier = Modifier.fillMaxSize(),
                    message = stringResource(R.string.feature_schedule_title),
                )
                ContentState.Empty -> EmptyState(
                    title = stringResource(
                        if (state.mode == ScheduleMode.Day) R.string.feature_schedule_empty_day
                        else if (state.filter.subjectId != null) R.string.feature_schedule_empty_filtered
                        else R.string.feature_schedule_empty_week,
                    ),
                    icon = StudentAiIcons.Schedule,
                    actionLabel = stringResource(R.string.feature_schedule_empty_action_add),
                    onAction = { viewModel.dispatch(ScheduleEvent.AddLessonClicked) },
                    modifier = Modifier.fillMaxSize(),
                )
                is ContentState.Error -> ErrorState(
                    title = stringResource(R.string.feature_schedule_title),
                    message = s.error.message ?: "—",
                    onRetry = { viewModel.dispatch(ScheduleEvent.RetryClicked) },
                    modifier = Modifier.fillMaxSize(),
                )
                is ContentState.Success -> if (state.mode == ScheduleMode.Day) {
                    DayList(
                        items = s.data,
                        onItemClick = { viewModel.dispatch(ScheduleEvent.LessonClicked(it)) },
                        onDelete = { viewModel.dispatch(ScheduleEvent.DeleteLessonRequested(it)) },
                    )
                } else {
                    WeekListGroupedByDay(
                        items = s.data,
                        onItemClick = { viewModel.dispatch(ScheduleEvent.LessonClicked(it)) },
                        onDelete = { viewModel.dispatch(ScheduleEvent.DeleteLessonRequested(it)) },
                    )
                }
            }
        }
    }
}
