package ru.studentai.tests.tasks.support

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import ru.studentai.feature.tasks.data.local.dao.TaskDao
import ru.studentai.feature.tasks.data.local.entity.TaskEntity

internal class FakeTaskDao : TaskDao() {

    val observeFlow: MutableStateFlow<List<TaskEntity>> = MutableStateFlow(emptyList())
    val subjectsFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    var getByIdResult: TaskEntity? = TaskFixtures.studentEntity()
    var upcomingResult: List<TaskEntity> = listOf(TaskFixtures.studentEntity())

    var observeCallCount: Int = 0
    var observeSubjectsCallCount: Int = 0
    var getByIdCallCount: Int = 0
    var getUpcomingCallCount: Int = 0
    var deleteByIdCallCount: Int = 0
    var upsertCallCount: Int = 0
    var upsertAllCallCount: Int = 0

    var lastObserveOwnerUserId: String? = null
    var lastObserveRole: String? = null
    var lastObserveSubjectId: String? = null
    var lastObserveShowCompleted: Boolean? = null
    var lastObserveOnlyOverdue: Boolean? = null
    var lastObserveNow: LocalDateTime? = null
    var lastSubjectsOwnerUserId: String? = null
    var lastSubjectsRole: String? = null
    var lastGetByIdId: String? = null
    var lastUpcomingOwnerUserId: String? = null
    var lastUpcomingRole: String? = null
    var lastUpcomingNow: LocalDateTime? = null
    var lastUpcomingLimit: Int? = null
    var lastDeleteById: String? = null
    var lastUpsertItem: TaskEntity? = null
    var lastUpsertAllItems: List<TaskEntity> = emptyList()

    override fun observe(
        ownerUserId: String,
        role: String,
        subjectId: String?,
        showCompleted: Boolean,
        onlyOverdue: Boolean,
        now: LocalDateTime,
    ): Flow<List<TaskEntity>> {
        observeCallCount += 1
        lastObserveOwnerUserId = ownerUserId
        lastObserveRole = role
        lastObserveSubjectId = subjectId
        lastObserveShowCompleted = showCompleted
        lastObserveOnlyOverdue = onlyOverdue
        lastObserveNow = now
        return observeFlow
    }

    override fun observeSubjects(ownerUserId: String, role: String): Flow<List<String>> {
        observeSubjectsCallCount += 1
        lastSubjectsOwnerUserId = ownerUserId
        lastSubjectsRole = role
        return subjectsFlow
    }

    override suspend fun getById(id: String): TaskEntity? {
        getByIdCallCount += 1
        lastGetByIdId = id
        return getByIdResult
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        role: String,
        now: LocalDateTime,
        limit: Int,
    ): List<TaskEntity> {
        getUpcomingCallCount += 1
        lastUpcomingOwnerUserId = ownerUserId
        lastUpcomingRole = role
        lastUpcomingNow = now
        lastUpcomingLimit = limit
        return upcomingResult
    }

    override suspend fun deleteById(id: String) {
        deleteByIdCallCount += 1
        lastDeleteById = id
    }

    override suspend fun insert(item: TaskEntity): Long = 1L

    override suspend fun insertAll(items: List<TaskEntity>): List<Long> = List(items.size) { 1L }

    override suspend fun update(item: TaskEntity): Int = 1

    override suspend fun updateAll(items: List<TaskEntity>): Int = items.size

    override suspend fun delete(item: TaskEntity): Int = 1

    override suspend fun deleteAll(items: List<TaskEntity>): Int = items.size

    override suspend fun upsert(item: TaskEntity) {
        upsertCallCount += 1
        lastUpsertItem = item
    }

    override suspend fun upsertAll(items: List<TaskEntity>) {
        upsertAllCallCount += 1
        lastUpsertAllItems = items
    }
}
