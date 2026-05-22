package ru.studentai.feature.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.database.dao.BaseDao
import ru.studentai.feature.schedule.data.local.entity.SubjectEntity

/** DAO для предметов. */
@Dao
public abstract class SubjectDao : BaseDao<SubjectEntity>() {

    @Query("SELECT * FROM subjects WHERE ownerUserId = :ownerUserId ORDER BY name COLLATE NOCASE ASC")
    public abstract fun observeAll(ownerUserId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    public abstract suspend fun getById(id: String): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE ownerUserId = :ownerUserId AND name = :name LIMIT 1")
    public abstract suspend fun findByName(ownerUserId: String, name: String): SubjectEntity?
}
