package ru.studentai.feature.schedule.presentation.edit

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.schedule.data.repository.ScheduleRepositoryImpl
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.usecase.GetScheduleItemUseCase
import ru.studentai.feature.schedule.domain.usecase.UpsertScheduleItemUseCase

@HiltViewModel
public class LessonEditViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val getItem: GetScheduleItemUseCase,
    private val upsert: UpsertScheduleItemUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<LessonEditState, LessonEditEvent, LessonEditEffect>(
    initialState = LessonEditState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var userId: String = ""

    override fun handleEvent(event: LessonEditEvent) {
        when (event) {
            is LessonEditEvent.Init -> initFor(event.itemId)
            is LessonEditEvent.SubjectChanged -> updateForm { it.copy(subjectName = event.value) }
            is LessonEditEvent.LessonTypeChanged -> updateForm { it.copy(lessonType = event.value) }
            is LessonEditEvent.CustomTypeLabelChanged -> updateForm { it.copy(customTypeLabel = event.value) }
            is LessonEditEvent.DateChanged -> updateForm { it.copy(date = event.value) }
            is LessonEditEvent.StartTimeChanged -> updateForm { it.copy(startTime = event.value) }
            is LessonEditEvent.EndTimeChanged -> updateForm { it.copy(endTime = event.value) }
            is LessonEditEvent.RoomChanged -> updateForm { it.copy(room = event.value) }
            is LessonEditEvent.TeacherChanged -> updateForm { it.copy(teacher = event.value) }
            is LessonEditEvent.NoteChanged -> updateForm { it.copy(note = event.value) }
            LessonEditEvent.SaveClicked -> save()
            LessonEditEvent.CancelClicked -> sendEffect(LessonEditEffect.Cancelled)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(LessonEditEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun initFor(itemId: String?) {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> userId = profile.value.user.id
                is DomainResult.Failure -> {
                    defaultErrorHandler(profile.error)
                    return@launchSafe
                }
            }

            if (itemId == null) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                updateState {
                    it.copy(
                        form = LessonFormState(
                            itemId = null,
                            date = now.date,
                            startTime = LocalTime(hour = now.hour.coerceIn(0, 22), minute = 0),
                            endTime = LocalTime(hour = (now.hour + 2).coerceIn(1, 23), minute = 0),
                        ),
                    )
                }
                return@launchSafe
            }

            updateState { it.copy(isLoading = true) }
            when (val r = getItem(itemId)) {
                is DomainResult.Success -> updateState {
                    it.copy(isLoading = false, form = r.value.toFormState())
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isLoading = false, loadFailed = true) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun save() {
        val form = currentState.form
        val errors = validate(form)
        if (errors.subjectError != null || errors.timeError != null) {
            updateState { it.copy(subjectError = errors.subjectError, timeError = errors.timeError) }
            return
        }
        updateState { it.copy(isSaving = true, subjectError = null, timeError = null) }

        launchSafe {
            val date = form.date ?: return@launchSafe
            val start = form.startTime ?: return@launchSafe
            val end = form.endTime ?: return@launchSafe
            val ownerId = userId.takeIf { it.isNotBlank() } ?: return@launchSafe

            val item = ScheduleItem(
                id = form.itemId ?: ScheduleRepositoryImpl.generateId(),
                ownerUserId = ownerId,
                subjectId = form.itemId?.let { "" } ?: "",
                subjectName = form.subjectName.trim(),
                lessonType = form.lessonType,
                customTypeLabel = form.customTypeLabel.trim().ifBlank { null }
                    .takeIf { form.lessonType == LessonType.Other },
                startAt = date.atTime(start),
                endAt = date.atTime(end),
                room = form.room.trim().ifBlank { null },
                teacher = form.teacher.trim().ifBlank { null },
                note = form.note.trim().ifBlank { null },
            )
            when (val r = upsert(item)) {
                is DomainResult.Success -> {
                    updateState { it.copy(isSaving = false) }
                    sendEffect(LessonEditEffect.Saved)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isSaving = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun validate(form: LessonFormState): ValidationOutcome {
        val subjectErr = if (form.subjectName.isBlank()) {
            "Укажите предмет"
        } else null
        val timeErr = when {
            form.date == null || form.startTime == null || form.endTime == null ->
                "Укажите дату и время"
            form.startTime >= form.endTime ->
                "Время окончания должно быть позже"
            else -> null
        }
        return ValidationOutcome(subjectErr, timeErr)
    }

    private fun updateForm(transform: (LessonFormState) -> LessonFormState) {
        updateState { it.copy(form = transform(it.form)) }
    }

    private fun ScheduleItem.toFormState(): LessonFormState = LessonFormState(
        itemId = id,
        subjectName = subjectName,
        lessonType = lessonType,
        customTypeLabel = customTypeLabel.orEmpty(),
        date = startAt.date,
        startTime = startAt.time,
        endTime = endAt.time,
        room = room.orEmpty(),
        teacher = teacher.orEmpty(),
        note = note.orEmpty(),
    )

    private val LocalDateTime.time: LocalTime
        get() = LocalTime(hour = hour, minute = minute, second = second, nanosecond = nanosecond)

    private data class ValidationOutcome(
        val subjectError: String?,
        val timeError: String?,
    )
}
