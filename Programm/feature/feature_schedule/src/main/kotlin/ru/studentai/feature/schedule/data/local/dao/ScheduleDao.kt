package ru.studentai.feature.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import ru.studentai.core.database.dao.BaseDao
import ru.studentai.feature.schedule.data.local.entity.ScheduleItemEntity

/**
 * DAO для занятий расписания.
 *
 * Все запросы — параметрические с `ownerUserId`, чтобы один пользователь не видел
 * данные другого (ТЗ §4.1.7: ролевое разграничение на уровне БД).
 */
@Dao
public abstract class ScheduleDao : BaseDao<ScheduleItemEntity>() {

    @Query(
        """
        SELECT * FROM schedule_items
        WHERE ownerUserId = :ownerUserId
          AND startAt >= :from AND startAt < :until
          AND (:subjectId IS NULL OR subjectId = :subjectId)
        ORDER BY startAt ASC
        """,
    )
    public abstract fun observeBetween(
        ownerUserId: String,
        from: LocalDateTime,
        until: LocalDateTime,
        subjectId: String?,
    ): Flow<List<ScheduleItemEntity>>

    @Query("SELECT * FROM schedule_items WHERE id = :id")
    public abstract suspend fun getById(id: String): ScheduleItemEntity?

    @Query(
        """
        SELECT * FROM schedule_items
        WHERE ownerUserId = :ownerUserId
          AND startAt > :now
        ORDER BY startAt ASC
        LIMIT 1
        """,
    )
    public abstract suspend fun getUpcoming(
        ownerUserId: String,
        now: LocalDateTime,
    ): ScheduleItemEntity?

    @Query("DELETE FROM schedule_items WHERE id = :id")
    public abstract suspend fun deleteById(id: String)

    @Query("DELETE FROM schedule_items WHERE ownerUserId = :ownerUserId")
    public abstract suspend fun deleteAllForOwner(ownerUserId: String)
}
