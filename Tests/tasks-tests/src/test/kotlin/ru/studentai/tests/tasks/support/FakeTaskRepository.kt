package ru.studentai.tests.tasks.support

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

internal class FakeTaskRepository : TaskRepository {

    val tasksFlow: MutableStateFlow<List<StudyTask>> = MutableStateFlow(emptyList())
    val subjectsFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    var getByIdResult: DomainResult<StudyTask> = DomainResult.Success(TaskFixtures.studentTask())
    var getUpcomingResult: DomainResult<List<StudyTask>> =
        DomainResult.Success(listOf(TaskFixtures.studentTask()))
    var upsertResult: DomainResult<StudyTask> = DomainResult.Success(TaskFixtures.studentTask())
    var toggleResult: DomainResult<StudyTask> = DomainResult.Success(
        TaskFixtures.studentTask(isCompleted = true, completedAt = TaskFixtures.dateTime("2026-05-26T11:00:00")),
    )
    var deleteResult: DomainResult<Unit> = DomainResult.Success(Unit)
    var refreshResult: DomainResult<Unit> = DomainResult.Success(Unit)

    var observeTasksCallCount: Int = 0
    var observeSubjectsCallCount: Int = 0
    var getByIdCallCount: Int = 0
    var getUpcomingCallCount: Int = 0
    var upsertCallCount: Int = 0
    var toggleCallCount: Int = 0
    var deleteCallCount: Int = 0
    var refreshCallCount: Int = 0

    var lastObserveOwnerUserId: String? = null
    var lastObserveRole: TaskRole? = null
    var lastObserveFilter: TaskFilter? = null
    var lastSubjectsOwnerUserId: String? = null
    var lastSubjectsRole: TaskRole? = null
    var lastGetByIdId: String? = null
    var lastUpcomingOwnerUserId: String? = null
    var lastUpcomingRole: TaskRole? = null
    var lastUpcomingLimit: Int? = null
    var lastUpsertTask: StudyTask? = null
    var lastToggleId: String? = null
    var lastDeleteId: String? = null
    var lastRefreshOwnerUserId: String? = null
    var lastRefreshRole: TaskRole? = null

    override fun observeTasks(
        ownerUserId: String,
        role: TaskRole,
        filter: TaskFilter,
    ): Flow<List<StudyTask>> {
        observeTasksCallCount += 1
        lastObserveOwnerUserId = ownerUserId
        lastObserveRole = role
        lastObserveFilter = filter
        return tasksFlow
    }

    override fun observeSubjects(ownerUserId: String, role: TaskRole): Flow<List<String>> {
        observeSubjectsCallCount += 1
        lastSubjectsOwnerUserId = ownerUserId
        lastSubjectsRole = role
        return subjectsFlow
    }

    override suspend fun getById(id: String): DomainResult<StudyTask> {
        getByIdCallCount += 1
        lastGetByIdId = id
        return getByIdResult
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        role: TaskRole,
        limit: Int,
    ): DomainResult<List<StudyTask>> {
        getUpcomingCallCount += 1
        lastUpcomingOwnerUserId = ownerUserId
        lastUpcomingRole = role
        lastUpcomingLimit = limit
        return getUpcomingResult
    }

    override suspend fun upsert(task: StudyTask): DomainResult<StudyTask> {
        upsertCallCount += 1
        lastUpsertTask = task
        return upsertResult
    }

    override suspend fun toggleCompletion(id: String): DomainResult<StudyTask> {
        toggleCallCount += 1
        lastToggleId = id
        return toggleResult
    }

    override suspend fun delete(id: String): DomainResult<Unit> {
        deleteCallCount += 1
        lastDeleteId = id
        return deleteResult
    }

    override suspend fun refresh(ownerUserId: String, role: TaskRole): DomainResult<Unit> {
        refreshCallCount += 1
        lastRefreshOwnerUserId = ownerUserId
        lastRefreshRole = role
        return refreshResult
    }
}
