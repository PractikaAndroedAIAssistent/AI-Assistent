package ru.studentai.feature.flashcards.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.create
import ru.studentai.core.database.factory.DatabaseFactory
import ru.studentai.feature.flashcards.data.local.dao.FlashcardDao
import ru.studentai.feature.flashcards.data.local.dao.FlashcardSetDao
import ru.studentai.feature.flashcards.data.local.database.FlashcardDatabase
import ru.studentai.feature.flashcards.data.remote.api.FlashcardsApi

@Module
@InstallIn(SingletonComponent::class)
public object FlashcardsDataModule {

    @Provides
    @Singleton
    public fun provideFlashcardDatabase(factory: DatabaseFactory): FlashcardDatabase =
        factory.build(name = FlashcardDatabase.NAME)

    @Provides
    @Singleton
    public fun provideFlashcardSetDao(database: FlashcardDatabase): FlashcardSetDao = database.setDao()

    @Provides
    @Singleton
    public fun provideFlashcardDao(database: FlashcardDatabase): FlashcardDao = database.cardDao()

    @Provides
    @Singleton
    public fun provideFlashcardsApi(retrofit: Retrofit): FlashcardsApi = retrofit.create()
}
