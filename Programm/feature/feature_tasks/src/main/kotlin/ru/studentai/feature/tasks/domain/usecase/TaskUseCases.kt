package ru.studentai.feature.tasks.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

public class ObserveTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public operator fun invoke(
        ownerUserId: String,
        role: TaskRole,
        filter: TaskFilter = TaskFilter.ALL,
    ): Flow<List<StudyTask>> = repository.observeTasks(ownerUserId, role, filter)
}

public class ObserveTaskSubjectsUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public operator fun invoke(ownerUserId: String, role: TaskRole): Flow<List<String>> =
        repository.observeSubjects(ownerUserId, role)
}

public class GetTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<StudyTask> =
        repository.getById(id)
}

public class GetUpcomingTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(
        ownerUserId: String,
        role: TaskRole,
        limit: Int,
    ): DomainResult<List<StudyTask>> = repository.getUpcoming(ownerUserId, role, limit)
}

public class UpsertTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(task: StudyTask): DomainResult<StudyTask> =
        repository.upsert(task)
}

public class ToggleTaskCompletionUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<StudyTask> =
        repository.toggleCompletion(id)
}

public class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<Unit> =
        repository.delete(id)
}

public class RefreshTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    public suspend operator fun invoke(ownerUserId: String, role: TaskRole): DomainResult<Unit> =
        repository.refresh(ownerUserId, role)
}
