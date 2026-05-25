package ru.studentai.feature.tasks.data.repository

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCallMapping
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.feature.tasks.data.local.dao.TaskDao
import ru.studentai.feature.tasks.data.mapper.role
import ru.studentai.feature.tasks.data.mapper.toDomain
import ru.studentai.feature.tasks.data.mapper.toEntity
import ru.studentai.feature.tasks.data.mapper.toUpsertRequest
import ru.studentai.feature.tasks.data.remote.api.TasksApi
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

@Singleton
public class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val api: TasksApi,
    private val errorMapper: HttpErrorMapper,
    private val dispatchers: DispatcherProvider,
) : TaskRepository {

    override fun observeTasks(
        ownerUserId: String,
        role: TaskRole,
        filter: TaskFilter,
    ): Flow<List<StudyTask>> = taskDao.observe(
        ownerUserId = ownerUserId,
        role = role.name,
        subjectId = filter.subjectId,
        showCompleted = filter.showCompleted,
        onlyOverdue = filter.onlyOverdue,
        now = nowLocal(),
    ).map { list -> list.map { it.toDomain() } }

    override fun observeSubjects(ownerUserId: String, role: TaskRole): Flow<List<String>> =
        taskDao.observeSubjects(ownerUserId, role.name)

    override suspend fun getById(id: String): DomainResult<StudyTask> = safeApi {
        taskDao.getById(id)?.toDomain()
            ?: throw StorageException.NotFound(entity = "Task", id = id)
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        role: TaskRole,
        limit: Int,
    ): DomainResult<List<StudyTask>> = safeApi {
        taskDao.getUpcoming(
            ownerUserId = ownerUserId,
            role = role.name,
            now = nowLocal(),
            limit = limit,
        ).map { it.toDomain() }
    }

    override suspend fun upsert(task: StudyTask): DomainResult<StudyTask> = safeApi {
        val saved = if (task.id.isBlank()) {
            api.create(task.toUpsertRequest()).toDomain(task.ownerUserId)
        } else {
            api.update(task.id, task.toUpsertRequest()).toDomain(task.ownerUserId)
        }
        taskDao.upsert(saved.toEntity())
        saved
    }

    override suspend fun toggleCompletion(id: String): DomainResult<StudyTask> = safeApi {
        val current = taskDao.getById(id)?.toDomain()
            ?: throw StorageException.NotFound(entity = "Task", id = id)
        val saved = api.toggle(id).toDomain(current.ownerUserId)
        taskDao.upsert(saved.toEntity())
        saved
    }

    override suspend fun delete(id: String): DomainResult<Unit> = safeApi {
        api.delete(id)
        taskDao.deleteById(id)
    }

    override suspend fun refresh(ownerUserId: String, role: TaskRole): DomainResult<Unit> = safeApi {
        val response = api.list(role.name)
        val entities = response.items.map { it.toDomain(ownerUserId).toEntity() }
        taskDao.upsertAll(entities)
    }

    private suspend inline fun <T> safeApi(crossinline block: suspend () -> T): DomainResult<T> =
        withContext(dispatchers.io) {
            safeCallMapping(mapper = errorMapper::map) { block() }
        }

    private fun nowLocal(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    public companion object {
        public fun generateId(): String = UUID.randomUUID().toString()
    }
}
