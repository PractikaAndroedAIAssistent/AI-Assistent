package com.example.assistentai.tasks

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskFilter
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

/**
 * Demo-реализация [TaskRepository] (in-memory).
 *
 * Сидирует начальный список: 4 дедлайна студента и 3 задачи преподавателя на ближайшие 7 дней.
 * Это даёт реальные данные на главной (блоки «Дедлайны на неделю» и «Задачи преподавателя»)
 * и на экране списка.
 */
@Singleton
public class DemoTaskRepository @Inject constructor() : TaskRepository {

    private val items: MutableStateFlow<List<StudyTask>> = MutableStateFlow(seed())

    override fun observeTasks(
        ownerUserId: String,
        role: TaskRole,
        filter: TaskFilter,
    ): Flow<List<StudyTask>> = items.map { list ->
        val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        list.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .filter { it.matchesRole(role) }
            .filter { filter.subjectId == null || it.subjectId == filter.subjectId }
            .filter { filter.showCompleted || !it.isCompleted }
            .filter { !filter.onlyOverdue || (!it.isCompleted && it.dueAt < nowLocal) }
    }

    override fun observeSubjects(ownerUserId: String, role: TaskRole): Flow<List<String>> =
        items.map { list ->
            list.asSequence()
                .filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
                .filter { it.matchesRole(role) }
                .mapNotNull { it.subjectName }
                .distinct()
                .sortedBy { it.lowercase() }
                .toList()
        }

    override suspend fun getById(id: String): DomainResult<StudyTask> {
        delay(SIMULATED_DELAY_MS / 2)
        return items.value.firstOrNull { it.id == id }
            ?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(StorageException.NotFound(entity = "Task", id = id))
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        role: TaskRole,
        limit: Int,
    ): DomainResult<List<StudyTask>> {
        val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val list = items.value
            .filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .filter { it.matchesRole(role) }
            .filter { !it.isCompleted }
            .filter { it.dueAt >= nowLocal }
            .sortedBy { it.dueAt }
            .take(limit)
        return DomainResult.Success(list)
    }

    override suspend fun upsert(task: StudyTask): DomainResult<StudyTask> {
        delay(SIMULATED_DELAY_MS)
        items.update { list ->
            val index = list.indexOfFirst { it.id == task.id }
            if (index >= 0) list.toMutableList().apply { set(index, task) } else list + task
        }
        return DomainResult.Success(task)
    }

    override suspend fun toggleCompletion(id: String): DomainResult<StudyTask> {
        delay(SIMULATED_DELAY_MS / 2)
        val current = items.value.firstOrNull { it.id == id }
            ?: return DomainResult.Failure(StorageException.NotFound(entity = "Task", id = id))
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val toggled = when (current) {
            is StudyTask.StudentDeadline -> current.copy(
                isCompleted = !current.isCompleted,
                completedAt = if (!current.isCompleted) now else null,
            )
            is StudyTask.TeacherAssignment -> current.copy(
                isCompleted = !current.isCompleted,
                completedAt = if (!current.isCompleted) now else null,
            )
        }
        items.update { list ->
            list.map { if (it.id == id) toggled else it }
        }
        return DomainResult.Success(toggled)
    }

    override suspend fun delete(id: String): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS / 2)
        items.update { list -> list.filterNot { it.id == id } }
        return DomainResult.Success(Unit)
    }

    override suspend fun refresh(ownerUserId: String, role: TaskRole): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS)
        return DomainResult.Success(Unit)
    }

    private fun StudyTask.matchesRole(role: TaskRole): Boolean = when (role) {
        TaskRole.Student -> this is StudyTask.StudentDeadline
        TaskRole.Teacher -> this is StudyTask.TeacherAssignment
    }

    private fun seed(): List<StudyTask> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val tomorrow = today.plus(DatePeriod(days = 1))
        val in3 = today.plus(DatePeriod(days = 3))
        val in5 = today.plus(DatePeriod(days = 5))
        val yesterday = today.plus(DatePeriod(days = -1))

        val studentDeadlines = listOf(
            StudyTask.StudentDeadline(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Алгоритмы",
                title = "Сдать лабораторную №2",
                description = "Реализовать алгоритм быстрой сортировки и протестировать.",
                dueAt = tomorrow.atTime(LocalTime(18, 0)),
                priority = TaskPriority.High,
            ),
            StudyTask.StudentDeadline(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Базы данных",
                title = "Подготовить ER-диаграмму",
                dueAt = in3.atTime(LocalTime(23, 59)),
                priority = TaskPriority.Normal,
            ),
            StudyTask.StudentDeadline(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Операционные системы",
                title = "Эссе про процессы и потоки",
                dueAt = in5.atTime(LocalTime(12, 0)),
                priority = TaskPriority.Low,
            ),
            StudyTask.StudentDeadline(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Высшая математика",
                title = "Контрольная по матанализу",
                dueAt = yesterday.atTime(LocalTime(10, 30)),
                priority = TaskPriority.Critical,
            ),
        )

        val teacherTasks = listOf(
            StudyTask.TeacherAssignment(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Алгоритмы",
                title = "Проверить лабораторные группы ИУ-101",
                dueAt = tomorrow.atTime(LocalTime(15, 0)),
                priority = TaskPriority.High,
                groupName = "ИУ-101",
            ),
            StudyTask.TeacherAssignment(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Базы данных",
                title = "Подготовить вопросы к семинару",
                dueAt = in3.atTime(LocalTime(9, 0)),
                priority = TaskPriority.Normal,
                groupName = "ИУ-202",
            ),
            StudyTask.TeacherAssignment(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = null,
                subjectName = "Операционные системы",
                title = "Загрузить материалы лекции",
                dueAt = in5.atTime(LocalTime(11, 0)),
                priority = TaskPriority.Low,
                groupName = "ИУ-301",
            ),
        )

        return studentDeadlines + teacherTasks
    }

    private companion object {
        const val OWNER_ALL = "*"
        const val SIMULATED_DELAY_MS = 250L
    }
}
