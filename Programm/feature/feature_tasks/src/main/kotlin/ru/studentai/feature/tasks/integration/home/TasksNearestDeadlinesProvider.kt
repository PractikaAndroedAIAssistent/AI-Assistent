package ru.studentai.feature.tasks.integration.home

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.home.domain.contract.NearestDeadlinesProvider
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.DeadlinePriority
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskPriority
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

/**
 * Адаптер `feature_home.NearestDeadlinesProvider` поверх [TaskRepository].
 *
 * Берёт ближайшие активные задачи студента и приводит к доменной модели главного
 * экрана. Просрочка вычисляется относительно `Clock.System.now()`.
 *
 * Биндинг регистрируется в `app/AppTasksModule`.
 */
@Singleton
public class TasksNearestDeadlinesProvider @Inject constructor(
    private val repository: TaskRepository,
) : NearestDeadlinesProvider {

    override suspend fun fetch(userId: String, limit: Int): List<DeadlineItem> {
        val result = repository.getUpcoming(
            ownerUserId = userId,
            role = TaskRole.Student,
            limit = limit,
        )
        val tasks = (result as? DomainResult.Success)?.value ?: return emptyList()
        val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return tasks.filterIsInstance<StudyTask.StudentDeadline>().map { task ->
            DeadlineItem(
                id = task.id,
                subject = task.subjectName ?: "—",
                title = task.title,
                dueAt = task.dueAt,
                priority = task.priority.toHomePriority(),
                isOverdue = !task.isCompleted && task.dueAt < nowLocal,
            )
        }
    }

    private fun TaskPriority.toHomePriority(): DeadlinePriority = when (this) {
        TaskPriority.Low -> DeadlinePriority.Low
        TaskPriority.Normal -> DeadlinePriority.Normal
        TaskPriority.High -> DeadlinePriority.High
        TaskPriority.Critical -> DeadlinePriority.Critical
    }
}
