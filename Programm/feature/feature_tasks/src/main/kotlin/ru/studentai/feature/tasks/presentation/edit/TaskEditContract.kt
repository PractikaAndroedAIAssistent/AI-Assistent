package ru.studentai.feature.tasks.presentation.edit

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole

@Immutable
public data class TaskFormState(
    val itemId: String? = null,
    val role: TaskRole = TaskRole.Student,
    val subjectName: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.Normal,
    val groupName: String = "",
)

@Immutable
public data class TaskEditState(
    val form: TaskFormState = TaskFormState(),
    val titleError: String? = null,
    val dueError: String? = null,
    val groupError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
) : UiState

public sealed interface TaskEditEvent : UiEvent {
    public data class Init(val itemId: String?) : TaskEditEvent
    public data class TitleChanged(val value: String) : TaskEditEvent
    public data class SubjectChanged(val value: String) : TaskEditEvent
    public data class DescriptionChanged(val value: String) : TaskEditEvent
    public data class DateChanged(val value: LocalDate) : TaskEditEvent
    public data class TimeChanged(val value: LocalTime) : TaskEditEvent
    public data class PriorityChanged(val value: TaskPriority) : TaskEditEvent
    public data class GroupChanged(val value: String) : TaskEditEvent
    public data object SaveClicked : TaskEditEvent
    public data object CancelClicked : TaskEditEvent
}

public sealed interface TaskEditEffect : UiEffect {
    public data object Saved : TaskEditEffect
    public data object Cancelled : TaskEditEffect
    public data class ShowMessage(val message: String) : TaskEditEffect
}
