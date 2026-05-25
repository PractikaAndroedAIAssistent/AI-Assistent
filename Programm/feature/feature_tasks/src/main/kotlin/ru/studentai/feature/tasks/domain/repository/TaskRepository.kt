package ru.studentai.feature.tasks.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskRole

/**
 * Domain-контракт задач (offline-first).
 */
public interface TaskRepository {

    /** Реактивный поток задач пользователя в указанной роли. */
    public fun observeTasks(
        ownerUserId: String,
        role: TaskRole,
        filter: TaskFilter = TaskFilter.ALL,
    ): Flow<List<StudyTask>>

    /** Реактивный поток уникальных названий предметов — для фильтр-чипов. */
    public fun observeSubjects(ownerUserId: String, role: TaskRole): Flow<List<String>>

    public suspend fun getById(id: String): DomainResult<StudyTask>

    /** Ближайшие активные задачи (для главной — feature_home). */
    public suspend fun getUpcoming(
        ownerUserId: String,
        role: TaskRole,
        limit: Int,
    ): DomainResult<List<StudyTask>>

    /** Создать или обновить задачу. */
    public suspend fun upsert(task: StudyTask): DomainResult<StudyTask>

    /** Отметить выполненной / снять отметку. */
    public suspend fun toggleCompletion(id: String): DomainResult<StudyTask>

    /** Удалить задачу по id. */
    public suspend fun delete(id: String): DomainResult<Unit>

    /** Pull-синхронизация с сервером. */
    public suspend fun refresh(ownerUserId: String, role: TaskRole): DomainResult<Unit>
}
