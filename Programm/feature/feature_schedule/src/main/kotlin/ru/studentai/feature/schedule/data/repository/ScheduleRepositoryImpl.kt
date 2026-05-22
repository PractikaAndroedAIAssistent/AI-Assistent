package ru.studentai.feature.schedule.data.repository

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCallMapping
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.feature.schedule.data.local.dao.ScheduleDao
import ru.studentai.feature.schedule.data.local.dao.SubjectDao
import ru.studentai.feature.schedule.data.mapper.toDomain
import ru.studentai.feature.schedule.data.mapper.toEntity
import ru.studentai.feature.schedule.data.mapper.toUpsertRequest
import ru.studentai.feature.schedule.data.remote.api.ScheduleApi
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository

/**
 * Production-реализация [ScheduleRepository] на Room + Retrofit.
 *
 * Контракты:
 *  • Source of truth — локальная БД; UI всегда наблюдает Flow из Room;
 *  • Mutations: сначала вызывается удалённый endpoint; при успехе локальный кэш
 *    обновляется ответом сервера (а не клиентским объектом) — это устраняет рассинхронизацию;
 *  • [refresh] идемпотентен — `upsertAll` локальных записей.
 *
 * Замечание: в текущем проекте реального backend нет, поэтому в app-модуле
 * этот класс заменяется на `DemoScheduleRepository` через Hilt-биндинг.
 * Здесь — production-готовый код, который заработает в момент подключения сервера.
 */
@Singleton
public class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val subjectDao: SubjectDao,
    private val api: ScheduleApi,
    private val errorMapper: HttpErrorMapper,
    private val dispatchers: DispatcherProvider,
) : ScheduleRepository {

    override fun observeDay(
        ownerUserId: String,
        date: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> = scheduleDao.observeBetween(
        ownerUserId = ownerUserId,
        from = date.atTime(LocalTime(0, 0)),
        until = date.plus(DatePeriod(days = 1)).atTime(LocalTime(0, 0)),
        subjectId = filter.subjectId,
    ).map { list -> list.map { it.toDomain() } }

    override fun observeWeek(
        ownerUserId: String,
        weekStart: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> {
        val mondayStart = weekStart.toMonday().atTime(LocalTime(0, 0))
        val nextMonday = weekStart.toMonday().plus(DatePeriod(days = 7)).atTime(LocalTime(0, 0))
        return scheduleDao.observeBetween(
            ownerUserId = ownerUserId,
            from = mondayStart,
            until = nextMonday,
            subjectId = filter.subjectId,
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun observeSubjects(ownerUserId: String): Flow<List<Subject>> =
        subjectDao.observeAll(ownerUserId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): DomainResult<ScheduleItem> = safeApi {
        val entity = scheduleDao.getById(id)
            ?: throw ru.studentai.core.common.error.StorageException.NotFound(
                entity = "ScheduleItem",
                id = id,
            )
        entity.toDomain()
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        now: Instant,
    ): DomainResult<ScheduleItem?> = safeApi {
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        scheduleDao.getUpcoming(ownerUserId, localNow)?.toDomain()
    }

    override suspend fun upsert(item: ScheduleItem): DomainResult<ScheduleItem> = safeApi {
        val saved = if (item.id.isBlank() || item.id == NEW_ID_PLACEHOLDER) {
            api.create(item.toUpsertRequest()).toDomain(item.ownerUserId)
        } else {
            api.update(item.id, item.toUpsertRequest()).toDomain(item.ownerUserId)
        }
        scheduleDao.upsert(saved.toEntity())
        saved
    }

    override suspend fun delete(id: String): DomainResult<Unit> = safeApi {
        api.delete(id)
        scheduleDao.deleteById(id)
    }

    override suspend fun refresh(ownerUserId: String): DomainResult<Unit> = safeApi {
        val response = api.sync()
        val subjects = response.subjects.map { it.toDomain().toEntity(ownerUserId) }
        val items = response.items.map { it.toDomain(ownerUserId).toEntity() }
        subjectDao.upsertAll(subjects)
        scheduleDao.upsertAll(items)
    }

    override suspend fun importFromUniversity(ownerUserId: String): DomainResult<Int> = safeApi {
        val response = api.importFromUniversity()
        // После импорта сервер уже обновил данные пользователя; синхронизируем локальный кэш.
        val sync = api.sync()
        subjectDao.upsertAll(sync.subjects.map { it.toDomain().toEntity(ownerUserId) })
        scheduleDao.upsertAll(sync.items.map { it.toDomain(ownerUserId).toEntity() })
        response.importedCount
    }

    private suspend inline fun <T> safeApi(crossinline block: suspend () -> T): DomainResult<T> =
        withContext(dispatchers.io) {
            safeCallMapping(mapper = errorMapper::map) { block() }
        }

    private fun LocalDate.toMonday(): LocalDate {
        val offset = (dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
        return if (offset == 0) this else minusDays(offset)
    }

    private fun LocalDate.minusDays(days: Int): LocalDate =
        this.plus(DatePeriod(days = -days))

    public companion object {
        /** Используется как маркер «новой записи без серверного id». */
        public const val NEW_ID_PLACEHOLDER: String = ""

        public fun generateId(): String = UUID.randomUUID().toString()
    }
}
