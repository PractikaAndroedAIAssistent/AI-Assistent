package ru.studentai.feature.schedule.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository

/** ТЗ §4.2.3: просмотр расписания по дням. */
public class ObserveScheduleForDayUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public operator fun invoke(
        ownerUserId: String,
        date: LocalDate,
        filter: ScheduleFilter = ScheduleFilter.NONE,
    ): Flow<List<ScheduleItem>> = repository.observeDay(ownerUserId, date, filter)
}

/** ТЗ §4.2.3: просмотр расписания по неделе. */
public class ObserveScheduleForWeekUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public operator fun invoke(
        ownerUserId: String,
        weekStart: LocalDate,
        filter: ScheduleFilter = ScheduleFilter.NONE,
    ): Flow<List<ScheduleItem>> = repository.observeWeek(ownerUserId, weekStart, filter)
}

/** Список предметов для фильтр-чипов и picker формы. */
public class ObserveSubjectsUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public operator fun invoke(ownerUserId: String): Flow<List<Subject>> =
        repository.observeSubjects(ownerUserId)
}

/** Получение одного занятия — для экрана редактирования. */
public class GetScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<ScheduleItem> =
        repository.getById(id)
}

/** Ближайшее занятие позже now — используется адаптером для feature_home. */
public class GetUpcomingLessonUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(
        ownerUserId: String,
        now: Instant,
    ): DomainResult<ScheduleItem?> = repository.getUpcoming(ownerUserId, now)
}

/** ТЗ §4.2.3: добавление/редактирование занятия (идемпотентный upsert). */
public class UpsertScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(item: ScheduleItem): DomainResult<ScheduleItem> =
        repository.upsert(item)
}

/** ТЗ §4.2.3: удаление занятия. */
public class DeleteScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(id: String): DomainResult<Unit> = repository.delete(id)
}

/** Pull-синхронизация с сервером. */
public class RefreshScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(ownerUserId: String): DomainResult<Unit> =
        repository.refresh(ownerUserId)
}

/** ТЗ §4.2.3: импорт расписания из ЛК вуза. Возвращает число импортированных занятий. */
public class ImportFromUniversityUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    public suspend operator fun invoke(ownerUserId: String): DomainResult<Int> =
        repository.importFromUniversity(ownerUserId)
}
