package ru.studentai.feature.schedule.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject

/**
 * Domain-контракт расписания (offline-first, ТЗ §4.1.4).
 *
 * Чтения возвращают `Flow` из локального источника (Room) → UI всегда видит свежие данные
 * после изменений. Записи и сетевые операции возвращают `DomainResult<…>`.
 */
public interface ScheduleRepository {

    /** Реактивный поток занятий в указанный день. */
    public fun observeDay(
        ownerUserId: String,
        date: LocalDate,
        filter: ScheduleFilter = ScheduleFilter.NONE,
    ): Flow<List<ScheduleItem>>

    /** Реактивный поток занятий за неделю, начиная с понедельника указанной даты. */
    public fun observeWeek(
        ownerUserId: String,
        weekStart: LocalDate,
        filter: ScheduleFilter = ScheduleFilter.NONE,
    ): Flow<List<ScheduleItem>>

    /** Реактивный поток всех предметов пользователя — для фильтр-чипов и picker'а. */
    public fun observeSubjects(ownerUserId: String): Flow<List<Subject>>

    /** Получает одно занятие (для экрана редактирования). */
    public suspend fun getById(id: String): DomainResult<ScheduleItem>

    /**
     * Получает ближайшее занятие, начинающееся позже [now]. Используется
     * [ru.studentai.feature.home.domain.contract.UpcomingLessonProvider]-адаптером.
     */
    public suspend fun getUpcoming(
        ownerUserId: String,
        now: kotlinx.datetime.Instant,
    ): DomainResult<ScheduleItem?>

    /** Вставляет или обновляет занятие (идемпотентно). */
    public suspend fun upsert(item: ScheduleItem): DomainResult<ScheduleItem>

    /** Удаляет занятие по идентификатору. */
    public suspend fun delete(id: String): DomainResult<Unit>

    /**
     * Принудительная синхронизация с сервером (pull). Идемпотентно — клиент использует
     * `upsertAll` локально, не дублируя записи. ТЗ §4.2.3: импорт расписания из ЛК вуза.
     */
    public suspend fun refresh(ownerUserId: String): DomainResult<Unit>

    /** Импорт расписания из ЛК вуза (ТЗ §4.2.3). Возвращает число импортированных занятий. */
    public suspend fun importFromUniversity(ownerUserId: String): DomainResult<Int>
}
