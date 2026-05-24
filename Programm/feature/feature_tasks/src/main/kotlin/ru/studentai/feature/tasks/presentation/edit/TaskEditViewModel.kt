package ru.studentai.feature.tasks.presentation.edit

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
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.tasks.data.repository.TaskRepositoryImpl
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.usecase.GetTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.UpsertTaskUseCase

@HiltViewModel
public class TaskEditViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val getTask: GetTaskUseCase,
    private val upsert: UpsertTaskUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<TaskEditState, TaskEditEvent, TaskEditEffect>(
    initialState = TaskEditState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var userId: String = ""

    override fun handleEvent(event: TaskEditEvent) {
        when (event) {
            is TaskEditEvent.Init -> initFor(event.itemId)
            is TaskEditEvent.TitleChanged -> updateForm { it.copy(title = event.value) }
            is TaskEditEvent.SubjectChanged -> updateForm { it.copy(subjectName = event.value) }
            is TaskEditEvent.DescriptionChanged -> updateForm { it.copy(description = event.value) }
            is TaskEditEvent.DateChanged -> updateForm { it.copy(dueDate = event.value) }
            is TaskEditEvent.TimeChanged -> updateForm { it.copy(dueTime = event.value) }
            is TaskEditEvent.PriorityChanged -> updateForm { it.copy(priority = event.value) }
            is TaskEditEvent.GroupChanged -> updateForm { it.copy(groupName = event.value) }
            TaskEditEvent.SaveClicked -> save()
            TaskEditEvent.CancelClicked -> sendEffect(TaskEditEffect.Cancelled)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(TaskEditEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun initFor(itemId: String?) {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> {
                    userId = profile.value.user.id
                    val role = profile.value.user.role.toTaskRole()
                    if (itemId == null) {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        updateState {
                            it.copy(
                                form = TaskFormState(
                                    role = role,
                                    dueDate = now.date,
                                    dueTime = LocalTime(hour = (now.hour + 1).coerceIn(0, 23), minute = 0),
                                ),
                            )
                        }
                        return@launchSafe
                    }
                    updateState { it.copy(isLoading = true) }
                    when (val r = getTask(itemId)) {
                        is DomainResult.Success -> updateState {
                            it.copy(isLoading = false, form = r.value.toFormState())
                        }
                        is DomainResult.Failure -> {
                            updateState { it.copy(isLoading = false) }
                            defaultErrorHandler(r.error)
                        }
                    }
                }
                is DomainResult.Failure -> defaultErrorHandler(profile.error)
            }
        }
    }

    private fun save() {
        val form = currentState.form
        val errors = validate(form)
        if (errors.hasAny()) {
            updateState {
                it.copy(
                    titleError = errors.title,
                    dueError = errors.due,
                    groupError = errors.group,
                )
            }
            return
        }
        updateState {
            it.copy(
                isSaving = true,
                titleError = null,
                dueError = null,
                groupError = null,
            )
        }
        launchSafe {
            val date = form.dueDate ?: return@launchSafe
            val time = form.dueTime ?: return@launchSafe
            val ownerId = userId.takeIf { it.isNotBlank() } ?: return@launchSafe
            val task: StudyTask = when (form.role) {
                TaskRole.Student -> StudyTask.StudentDeadline(
                    id = form.itemId ?: TaskRepositoryImpl.generateId(),
                    ownerUserId = ownerId,
                    subjectId = null,
                    subjectName = form.subjectName.trim().ifBlank { null },
                    title = form.title.trim(),
                    description = form.description.trim().ifBlank { null },
                    dueAt = date.atTime(time),
                    priority = form.priority,
                )
                TaskRole.Teacher -> StudyTask.TeacherAssignment(
                    id = form.itemId ?: TaskRepositoryImpl.generateId(),
                    ownerUserId = ownerId,
                    subjectId = null,
                    subjectName = form.subjectName.trim().ifBlank { null },
                    title = form.title.trim(),
                    description = form.description.trim().ifBlank { null },
                    dueAt = date.atTime(time),
                    priority = form.priority,
                    groupName = form.groupName.trim(),
                )
            }
            when (val r = upsert(task)) {
                is DomainResult.Success -> {
                    updateState { it.copy(isSaving = false) }
                    sendEffect(TaskEditEffect.Saved)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isSaving = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun validate(form: TaskFormState): ValidationOutcome {
        val titleErr = if (form.title.isBlank()) "Введите название задачи" else null
        val dueErr = when {
            form.dueDate == null || form.dueTime == null -> "Укажите срок"
            else -> null
        }
        val groupErr = when {
            form.role == TaskRole.Teacher && form.groupName.isBlank() -> "Укажите группу"
            else -> null
        }
        return ValidationOutcome(titleErr, dueErr, groupErr)
    }

    private fun updateForm(transform: (TaskFormState) -> TaskFormState) {
        updateState { it.copy(form = transform(it.form)) }
    }

    private fun StudyTask.toFormState(): TaskFormState = TaskFormState(
        itemId = id,
        role = when (this) {
            is StudyTask.StudentDeadline -> TaskRole.Student
            is StudyTask.TeacherAssignment -> TaskRole.Teacher
        },
        subjectName = subjectName.orEmpty(),
        title = title,
        description = description.orEmpty(),
        dueDate = dueAt.date,
        dueTime = LocalTime(hour = dueAt.hour, minute = dueAt.minute),
        priority = priority,
        groupName = (this as? StudyTask.TeacherAssignment)?.groupName.orEmpty(),
    )

    private fun UserRole.toTaskRole(): TaskRole = when (this) {
        UserRole.Student -> TaskRole.Student
        UserRole.Teacher -> TaskRole.Teacher
    }

    private data class ValidationOutcome(
        val title: String?,
        val due: String?,
        val group: String?,
    ) {
        fun hasAny(): Boolean = title != null || due != null || group != null
    }
}
