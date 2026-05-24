package ru.studentai.feature.flashcards.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import ru.studentai.core.database.dao.BaseDao
import ru.studentai.feature.flashcards.data.local.entity.FlashcardSetEntity

/**
 * Запросы выборки наборов с агрегацией counts.
 */
public data class FlashcardSetWithCounts(
    @androidx.room.Embedded val set: FlashcardSetEntity,
    @androidx.room.ColumnInfo(name = "card_count") val cardCount: Int,
    @androidx.room.ColumnInfo(name = "due_count") val dueCount: Int,
)

@Dao
public abstract class FlashcardSetDao : BaseDao<FlashcardSetEntity>() {

    /**
     * Поток наборов пользователя с подсчётом всех карточек и карточек, готовых к повторению
     * на дату [today]. Используется LEFT JOIN + COUNT для O(N) выборки.
     */
    @Query(
        """
        SELECT s.*,
               (SELECT COUNT(*) FROM flashcards WHERE setId = s.id) AS card_count,
               (SELECT COUNT(*) FROM flashcards
                 WHERE setId = s.id
                   AND (nextReviewAt IS NULL OR nextReviewAt <= :today)) AS due_count
        FROM flashcard_sets s
        WHERE s.ownerUserId = :ownerUserId
        ORDER BY s.updatedAt DESC
        """,
    )
    public abstract fun observeWithCounts(
        ownerUserId: String,
        today: LocalDate,
    ): Flow<List<FlashcardSetWithCounts>>

    @Query("SELECT * FROM flashcard_sets WHERE id = :id")
    public abstract suspend fun getById(id: String): FlashcardSetEntity?

    @Query("DELETE FROM flashcard_sets WHERE id = :id")
    public abstract suspend fun deleteById(id: String)
}
