package ru.studentai.feature.schedule.presentation.schedule

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject

@Immutable
public enum class ScheduleMode { Day, Week }

@Immutable
public data class ScheduleState(
    val mode: ScheduleMode = ScheduleMode.Day,
    val anchorDate: LocalDate? = null,
    val items: ContentState<List<ScheduleItem>> = ContentState.Idle,
    val subjects: List<Subject> = emptyList(),
    val filter: ScheduleFilter = ScheduleFilter.NONE,
    val isImporting: Boolean = false,
    val isRefreshing: Boolean = false,
) : UiState

public sealed interface ScheduleEvent : UiEvent {
    public data object Refresh : ScheduleEvent
    public data object RetryClicked : ScheduleEvent
    public data class ModeChanged(val mode: ScheduleMode) : ScheduleEvent
    public data class DateSelected(val date: LocalDate) : ScheduleEvent
    public data object TodayClicked : ScheduleEvent
    public data class FilterSubjectChanged(val subjectId: String?) : ScheduleEvent
    public data object ImportFromUniversityClicked : ScheduleEvent
    public data object AddLessonClicked : ScheduleEvent
    public data class LessonClicked(val id: String) : ScheduleEvent
    public data class DeleteLessonRequested(val id: String) : ScheduleEvent
}

public sealed interface ScheduleEffect : UiEffect {
    public data object NavigateToAddLesson : ScheduleEffect
    public data class NavigateToEditLesson(val id: String) : ScheduleEffect
    public data class ShowMessage(val message: String) : ScheduleEffect
}
