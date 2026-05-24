package ru.studentai.feature.tasks.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import ru.studentai.core.database.dao.BaseDao
import ru.studentai.feature.tasks.data.local.entity.TaskEntity

@Dao
public abstract class TaskDao : BaseDao<TaskEntity>() {

    /**
     * Базовый поток. Фильтрация по subjectId/onlyOverdue/showCompleted делается параметрически.
     * `now` приходит из репозитория, чтобы тесты могли передавать фиксированное значение.
     */
    @Query(
        """
        SELECT * FROM tasks
        WHERE ownerUserId = :ownerUserId
          AND role = :role
          AND (:subjectId IS NULL OR subjectId = :subjectId)
          AND (:showCompleted OR isCompleted = 0)
          AND (:onlyOverdue = 0 OR (isCompleted = 0 AND dueAt < :now))
        ORDER BY dueAt ASC
        """,
    )
    public abstract fun observe(
        ownerUserId: String,
        role: String,
        subjectId: String?,
        showCompleted: Boolean,
        onlyOverdue: Boolean,
        now: LocalDateTime,
    ): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT DISTINCT subjectName FROM tasks
        WHERE ownerUserId = :ownerUserId AND role = :role AND subjectName IS NOT NULL
        ORDER BY subjectName COLLATE NOCASE ASC
        """,
    )
    public abstract fun observeSubjects(ownerUserId: String, role: String): Flow<List<String>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    public abstract suspend fun getById(id: String): TaskEntity?

    @Query(
        """
        SELECT * FROM tasks
        WHERE ownerUserId = :ownerUserId
          AND role = :role
          AND isCompleted = 0
          AND dueAt >= :now
        ORDER BY dueAt ASC
        LIMIT :limit
        """,
    )
    public abstract suspend fun getUpcoming(
        ownerUserId: String,
        role: String,
        now: LocalDateTime,
        limit: Int,
    ): List<TaskEntity>

    @Query("DELETE FROM tasks WHERE id = :id")
    public abstract suspend fun deleteById(id: String)
}
