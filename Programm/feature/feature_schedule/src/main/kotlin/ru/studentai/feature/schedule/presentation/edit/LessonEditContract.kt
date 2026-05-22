package ru.studentai.feature.schedule.presentation.edit

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import ru.studentai.core.ui.mvi.UiEffect
import ru.studentai.core.ui.mvi.UiEvent
import ru.studentai.core.ui.mvi.UiState
import ru.studentai.feature.schedule.domain.model.LessonType

@Immutable
public data class LessonFormState(
    val itemId: String? = null,
    val subjectName: String = "",
    val lessonType: LessonType = LessonType.Lecture,
    val customTypeLabel: String = "",
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val room: String = "",
    val teacher: String = "",
    val note: String = "",
)

@Immutable
public data class LessonEditState(
    val form: LessonFormState = LessonFormState(),
    val subjectError: String? = null,
    val timeError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val loadFailed: Boolean = false,
) : UiState

public sealed interface LessonEditEvent : UiEvent {
    public data class Init(val itemId: String?) : LessonEditEvent
    public data class SubjectChanged(val value: String) : LessonEditEvent
    public data class LessonTypeChanged(val value: LessonType) : LessonEditEvent
    public data class CustomTypeLabelChanged(val value: String) : LessonEditEvent
    public data class DateChanged(val value: LocalDate) : LessonEditEvent
    public data class StartTimeChanged(val value: LocalTime) : LessonEditEvent
    public data class EndTimeChanged(val value: LocalTime) : LessonEditEvent
    public data class RoomChanged(val value: String) : LessonEditEvent
    public data class TeacherChanged(val value: String) : LessonEditEvent
    public data class NoteChanged(val value: String) : LessonEditEvent
    public data object SaveClicked : LessonEditEvent
    public data object CancelClicked : LessonEditEvent
}

public sealed interface LessonEditEffect : UiEffect {
    public data object Saved : LessonEditEffect
    public data object Cancelled : LessonEditEffect
    public data class ShowMessage(val message: String) : LessonEditEffect
}
