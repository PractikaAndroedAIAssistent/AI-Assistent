package ru.studentai.feature.flashcards.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.studentai.core.database.converter.InstantConverter
import ru.studentai.core.database.converter.LocalDateConverter
import ru.studentai.core.database.converter.LocalDateTimeConverter
import ru.studentai.feature.flashcards.data.local.dao.FlashcardDao
import ru.studentai.feature.flashcards.data.local.dao.FlashcardSetDao
import ru.studentai.feature.flashcards.data.local.entity.FlashcardEntity
import ru.studentai.feature.flashcards.data.local.entity.FlashcardSetEntity

@Database(
    entities = [FlashcardSetEntity::class, FlashcardEntity::class],
    version = FlashcardDatabase.VERSION,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
)
public abstract class FlashcardDatabase : RoomDatabase() {

    public abstract fun setDao(): FlashcardSetDao
    public abstract fun cardDao(): FlashcardDao

    public companion object {
        public const val NAME: String = "studentai_flashcards.db"
        public const val VERSION: Int = 1
    }
}
