package ru.studentai.feature.flashcards.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.studentai.core.database.dao.BaseDao
import ru.studentai.feature.flashcards.data.local.entity.FlashcardEntity

@Dao
public abstract class FlashcardDao : BaseDao<FlashcardEntity>() {

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY createdAt ASC")
    public abstract fun observeBySet(setId: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY createdAt ASC")
    public abstract suspend fun getAllInSet(setId: String): List<FlashcardEntity>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    public abstract suspend fun getById(id: String): FlashcardEntity?

    @Query("DELETE FROM flashcards WHERE id = :id")
    public abstract suspend fun deleteById(id: String)
}
