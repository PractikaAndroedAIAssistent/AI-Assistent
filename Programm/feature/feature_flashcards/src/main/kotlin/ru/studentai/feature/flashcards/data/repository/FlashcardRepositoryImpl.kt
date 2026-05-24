package ru.studentai.feature.flashcards.data.repository

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCallMapping
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.feature.flashcards.data.local.dao.FlashcardDao
import ru.studentai.feature.flashcards.data.local.dao.FlashcardSetDao
import ru.studentai.feature.flashcards.data.mapper.toDomain
import ru.studentai.feature.flashcards.data.mapper.toEntity
import ru.studentai.feature.flashcards.data.remote.api.FlashcardsApi
import ru.studentai.feature.flashcards.data.remote.dto.ReviewSubmitRequest
import ru.studentai.feature.flashcards.domain.algorithm.Sm2Algorithm
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.FlashcardSet
import ru.studentai.feature.flashcards.domain.model.ReviewQuality
import ru.studentai.feature.flashcards.domain.repository.FlashcardRepository

@Singleton
public class FlashcardRepositoryImpl @Inject constructor(
    private val setDao: FlashcardSetDao,
    private val cardDao: FlashcardDao,
    private val api: FlashcardsApi,
    private val algorithm: Sm2Algorithm,
    private val errorMapper: HttpErrorMapper,
    private val dispatchers: DispatcherProvider,
) : FlashcardRepository {

    override fun observeSets(ownerUserId: String): Flow<List<FlashcardSet>> {
        val today = todayLocal()
        return setDao.observeWithCounts(ownerUserId, today).map { list -> list.map { it.toDomain() } }
    }

    override fun observeCardsInSet(setId: String): Flow<List<Flashcard>> =
        cardDao.observeBySet(setId).map { list -> list.map { it.toDomain() } }

    override suspend fun getSet(setId: String): DomainResult<FlashcardSet> = safeApi {
        val today = todayLocal()
        val entity = setDao.getById(setId)
            ?: throw StorageException.NotFound(entity = "FlashcardSet", id = setId)
        val cards = cardDao.getAllInSet(setId)
        FlashcardSet(
            id = entity.id,
            ownerUserId = entity.ownerUserId,
            name = entity.name,
            subjectName = entity.subjectName,
            cardCount = cards.size,
            dueCount = cards.count { it.nextReviewAt == null || it.nextReviewAt <= today },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    override suspend fun upsertSet(set: FlashcardSet): DomainResult<FlashcardSet> = safeApi {
        // В demo-режиме репозиторий не вызывает api — это решает app-биндинг.
        // Production-реализация (этот класс): сначала сервер, потом локальный кэш.
        val savedDto = if (isNewId(set.id)) {
            api.createSet(ru.studentai.feature.flashcards.data.remote.dto.UpsertSetRequest(set.name, set.subjectName))
        } else {
            api.updateSet(set.id, ru.studentai.feature.flashcards.data.remote.dto.UpsertSetRequest(set.name, set.subjectName))
        }
        val savedDomain = savedDto.toDomain(set.ownerUserId)
        setDao.upsert(savedDomain.toEntity())
        savedDomain
    }

    override suspend fun deleteSet(setId: String): DomainResult<Unit> = safeApi {
        api.deleteSet(setId)
        setDao.deleteById(setId)
    }

    override suspend fun upsertCard(card: Flashcard): DomainResult<Flashcard> = safeApi {
        val req = ru.studentai.feature.flashcards.data.remote.dto.UpsertCardRequest(
            setId = card.setId,
            front = card.front,
            back = card.back,
        )
        val savedDto = if (isNewId(card.id)) api.createCard(req) else api.updateCard(card.id, req)
        val savedDomain = savedDto.toDomain(card.ownerUserId)
        cardDao.upsert(savedDomain.toEntity())
        savedDomain
    }

    override suspend fun deleteCard(id: String): DomainResult<Unit> = safeApi {
        api.deleteCard(id)
        cardDao.deleteById(id)
    }

    override suspend fun submitReview(
        cardId: String,
        quality: ReviewQuality,
    ): DomainResult<Flashcard> = safeApi {
        val current = cardDao.getById(cardId)?.toDomain()
            ?: throw StorageException.NotFound(entity = "Flashcard", id = cardId)
        val updated = algorithm.apply(current, quality)
        api.submitReview(cardId, ReviewSubmitRequest(qualityScore = quality.score))
        cardDao.upsert(updated.toEntity())
        updated
    }

    override suspend fun refresh(ownerUserId: String): DomainResult<Unit> = safeApi {
        val response = api.sync()
        setDao.upsertAll(response.sets.map { it.toDomain(ownerUserId).toEntity() })
        cardDao.upsertAll(response.cards.map { it.toDomain(ownerUserId).toEntity() })
    }

    private suspend inline fun <T> safeApi(crossinline block: suspend () -> T): DomainResult<T> =
        withContext(dispatchers.io) {
            safeCallMapping(mapper = errorMapper::map) { block() }
        }

    private fun todayLocal(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun isNewId(id: String): Boolean = id.isBlank()

    public companion object {
        public fun generateId(): String = UUID.randomUUID().toString()
    }
}
