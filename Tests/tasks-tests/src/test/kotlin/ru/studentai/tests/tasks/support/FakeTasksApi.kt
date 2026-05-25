package ru.studentai.tests.tasks.support

import ru.studentai.feature.tasks.data.remote.api.TasksApi
import ru.studentai.feature.tasks.data.remote.dto.TaskDto
import ru.studentai.feature.tasks.data.remote.dto.TaskListResponse
import ru.studentai.feature.tasks.data.remote.dto.UpsertTaskRequest

internal class FakeTasksApi : TasksApi {

    var listResponse: TaskListResponse = TaskFixtures.taskListResponse(listOf(TaskFixtures.studentDto()))
    var createResponse: TaskDto = TaskFixtures.studentDto(id = "created-1")
    var updateResponse: TaskDto = TaskFixtures.studentDto(id = "updated-1")
    var toggleResponse: TaskDto = TaskFixtures.studentDto(id = "toggled-1", isCompleted = true, completedAt = "2026-05-26T11:00:00")

    var listThrowable: Throwable? = null
    var createThrowable: Throwable? = null
    var updateThrowable: Throwable? = null
    var toggleThrowable: Throwable? = null
    var deleteThrowable: Throwable? = null

    var listCallCount: Int = 0
    var createCallCount: Int = 0
    var updateCallCount: Int = 0
    var toggleCallCount: Int = 0
    var deleteCallCount: Int = 0

    var lastListRole: String? = null
    var lastCreateRequest: UpsertTaskRequest? = null
    var lastUpdateId: String? = null
    var lastUpdateRequest: UpsertTaskRequest? = null
    var lastToggleId: String? = null
    var lastDeleteId: String? = null

    override suspend fun list(role: String): TaskListResponse {
        listCallCount += 1
        lastListRole = role
        listThrowable?.let { throw it }
        return listResponse
    }

    override suspend fun create(request: UpsertTaskRequest): TaskDto {
        createCallCount += 1
        lastCreateRequest = request
        createThrowable?.let { throw it }
        return createResponse
    }

    override suspend fun update(id: String, request: UpsertTaskRequest): TaskDto {
        updateCallCount += 1
        lastUpdateId = id
        lastUpdateRequest = request
        updateThrowable?.let { throw it }
        return updateResponse
    }

    override suspend fun toggle(id: String): TaskDto {
        toggleCallCount += 1
        lastToggleId = id
        toggleThrowable?.let { throw it }
        return toggleResponse
    }

    override suspend fun delete(id: String) {
        deleteCallCount += 1
        lastDeleteId = id
        deleteThrowable?.let { throw it }
    }
}
