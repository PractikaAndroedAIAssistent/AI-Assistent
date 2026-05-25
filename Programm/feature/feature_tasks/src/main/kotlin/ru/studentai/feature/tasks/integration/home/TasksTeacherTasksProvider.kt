package ru.studentai.feature.tasks.integration.home

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.home.domain.contract.TeacherTasksProvider
import ru.studentai.feature.home.domain.model.TeacherTask
import ru.studentai.feature.tasks.domain.model.StudyTask
import ru.studentai.feature.tasks.domain.model.TaskRole
import ru.studentai.feature.tasks.domain.repository.TaskRepository

/**
 * Адаптер `feature_home.TeacherTasksProvider` поверх [TaskRepository].
 *
 * Берёт ближайшие активные задачи преподавателя и приводит к доменной модели
 * главного экрана.
 */
@Singleton
public class TasksTeacherTasksProvider @Inject constructor(
    private val repository: TaskRepository,
) : TeacherTasksProvider {

    override suspend fun fetch(userId: String, limit: Int): List<TeacherTask> {
        val result = repository.getUpcoming(
            ownerUserId = userId,
            role = TaskRole.Teacher,
            limit = limit,
        )
        val tasks = (result as? DomainResult.Success)?.value ?: return emptyList()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return tasks.filterIsInstance<StudyTask.TeacherAssignment>().map { task ->
            TeacherTask(
                id = task.id,
                title = task.title,
                dueAt = task.dueAt,
                relatedSubject = task.subjectName,
                isOverdue = !task.isCompleted && task.dueAt < now,
            )
        }
    }
}
