package ru.studentai.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update

/**
 * Базовый абстрактный DAO для CRUD-операций.
 *
 * Конкретные DAO фич наследуют его и добавляют свои `@Query`-методы:
 *
 * ```
 * @Dao
 * abstract class NoteDao : BaseDao<NoteEntity>() {
 *     @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY updatedAt DESC")
 *     abstract fun observeNotes(userId: String): Flow<List<NoteEntity>>
 *
 *     @Query("DELETE FROM notes WHERE id = :id")
 *     abstract suspend fun deleteById(id: String)
 * }
 * ```
 *
 * Все базовые методы — `suspend`. Если нужны reactive-запросы — конкретный DAO добавляет
 * `Flow`-методы со своими `@Query`.
 *
 * `upsert` реализован вручную через [Insert] с REPLACE + [Update] в [Transaction],
 * что устойчиво к старым SQLite-движкам, где нет нативного UPSERT.
 */
public abstract class BaseDao<T : Any> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract suspend fun insertAll(items: List<T>): List<Long>

    @Update
    public abstract suspend fun update(item: T): Int

    @Update
    public abstract suspend fun updateAll(items: List<T>): Int

    @Delete
    public abstract suspend fun delete(item: T): Int

    @Delete
    public abstract suspend fun deleteAll(items: List<T>): Int

    /**
     * Идемпотентный upsert: пытается вставить; если уже существует (PK conflict) — обновляет.
     */
    @Transaction
    public open suspend fun upsert(item: T) {
        val id = insert(item)
        if (id == -1L) update(item)
    }

    @Transaction
    public open suspend fun upsertAll(items: List<T>) {
        val ids = insertAll(items)
        val toUpdate = items.filterIndexed { index, _ -> ids[index] == -1L }
        if (toUpdate.isNotEmpty()) updateAll(toUpdate)
    }
}
