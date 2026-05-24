package ru.studentai.feature.tasks.presentation.list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.model.TaskSort
import ru.studentai.feature.tasks.domain.usecase.DeleteTaskUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTaskSubjectsUseCase
import ru.studentai.feature.tasks.domain.usecase.ObserveTasksUseCase
import ru.studentai.feature.tasks.domain.usecase.ToggleTaskCompletionUseCase

@HiltViewModel
public class TasksViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val observeTasks: ObserveTasksUseCase,
    private val observeSubjects: ObserveTaskSubjectsUseCase,
    private val toggleCompletion: ToggleTaskCompletionUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<TasksState, TasksEvent, TasksEffect>(
    initialState = TasksState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var observeJob: Job? = null
    private var userId: String = ""

    init {
        loadProfileAndObserve()
    }

    override fun handleEvent(event: TasksEvent) {
        when (event) {
            TasksEvent.RetryClicked -> loadProfileAndObserve()
            is TasksEvent.SortChanged -> {
                updateState { it.copy(sort = event.value) }
            }
            is TasksEvent.FilterPresetChanged -> {
                updateState { it.copy(preset = event.preset) }
                restartObserve()
            }
            is TasksEvent.SubjectFilterChanged -> {
                updateState { it.copy(subjectFilter = event.subject) }
                restartObserve()
            }
            is TasksEvent.ToggleCompletion -> toggle(event.id)
            is TasksEvent.Delete -> delete(event.id)
            is TasksEvent.TaskClicked -> sendEffect(TasksEffect.NavigateToEdit(event.id))
            TasksEvent.AddClicked -> sendEffect(TasksEffect.NavigateToAdd)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(TasksEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun loadProfileAndObserve() {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> {
                    userId = profile.value.user.id
                    val role = profile.value.user.role.toTaskRole()
                    updateState { it.copy(role = role) }
                    restartObserve()
                }
                is DomainResult.Failure -> defaultErrorHandler(profile.error)
            }
        }
    }

    private fun restartObserve() {
        observeJob?.cancel()
        val ownerId = userId.takeIf { it.isNotBlank() } ?: return
        val role = currentState.role
        val filter = currentState.filter

        updateState { it.copy(items = ContentState.Loading) }

        observeJob = combine(
            observeTasks(ownerId, role, filter),
            observeSubjects(ownerId, role),
        ) { items, subjects ->
            val sorted = sort(items, currentState.sort)
            // Для preset == Completed дополнительно показываем только выполненные
            val finalList = if (currentState.preset == TaskListFilterPreset.Completed) {
                sorted.filter { it.isCompleted }
            } else {
                sorted
            }
            finalList to subjects
        }
            .onEach { (list, subjects) ->
                updateState {
                    it.copy(
                        items = if (list.isEmpty()) ContentState.Empty else ContentState.Success(list),
                        subjects = subjects,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun toggle(id: String) {
        launchSafe {
            when (val r = toggleCompletion(id)) {
                is DomainResult.Success -> Unit
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }

    private fun delete(id: String) {
        launchSafe {
            when (val r = deleteTask(id)) {
                is DomainResult.Success -> Unit
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }

    private fun sort(items: List<StudyTask>, sort: TaskSort): List<StudyTask> = when (sort) {
        TaskSort.ByDate -> items.sortedBy { it.dueAt }
        TaskSort.BySubject -> items.sortedWith(
            compareBy<StudyTask> { it.subjectName ?: "" }.thenBy { it.dueAt },
        )
        TaskSort.ByPriority -> items.sortedWith(
            compareByDescending<StudyTask> { it.priority.ordinal }.thenBy { it.dueAt },
        )
    }

    private fun UserRole.toTaskRole(): TaskRole = when (this) {
        UserRole.Student -> TaskRole.Student
        UserRole.Teacher -> TaskRole.Teacher
    }
}
