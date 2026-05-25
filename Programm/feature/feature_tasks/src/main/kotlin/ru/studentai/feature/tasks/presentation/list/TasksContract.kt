package ru.studentai.feature.tasks.presentation.list

import androidx.compose.runtime.Immutable
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.model.TaskSort

@Immutable
public enum class TaskListFilterPreset { All, Active, Overdue, Completed }

@Immutable
public data class TasksState(
    val role: TaskRole = TaskRole.Student,
    val items: ContentState<List<StudyTask>> = ContentState.Idle,
    val subjects: List<String> = emptyList(),
    val sort: TaskSort = TaskSort.ByDate,
    val preset: TaskListFilterPreset = TaskListFilterPreset.Active,
    val subjectFilter: String? = null,
) : UiState {
    public val filter: TaskFilter
        get() = when (preset) {
            TaskListFilterPreset.All -> TaskFilter(subjectId = subjectFilter, showCompleted = true)
            TaskListFilterPreset.Active -> TaskFilter(subjectId = subjectFilter, showCompleted = false)
            TaskListFilterPreset.Overdue -> TaskFilter(
                subjectId = subjectFilter,
                showCompleted = false,
                onlyOverdue = true,
            )
            TaskListFilterPreset.Completed -> TaskFilter(
                subjectId = subjectFilter,
                showCompleted = true,
            )
        }
}

public sealed interface TasksEvent : UiEvent {
    public data object RetryClicked : TasksEvent
    public data class SortChanged(val value: TaskSort) : TasksEvent
    public data class FilterPresetChanged(val preset: TaskListFilterPreset) : TasksEvent
    public data class SubjectFilterChanged(val subject: String?) : TasksEvent
    public data class ToggleCompletion(val id: String) : TasksEvent
    public data class Delete(val id: String) : TasksEvent
    public data class TaskClicked(val id: String) : TasksEvent
    public data object AddClicked : TasksEvent
}

public sealed interface TasksEffect : UiEffect {
    public data object NavigateToAdd : TasksEffect
    public data class NavigateToEdit(val id: String) : TasksEffect
    public data class ShowMessage(val message: String) : TasksEffect
}
