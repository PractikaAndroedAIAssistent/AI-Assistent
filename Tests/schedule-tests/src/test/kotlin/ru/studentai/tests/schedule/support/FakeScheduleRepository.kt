package ru.studentai.tests.schedule.support

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository

internal class FakeScheduleRepository : ScheduleRepository {

    val dayFlow: MutableStateFlow<List<ScheduleItem>> = MutableStateFlow(emptyList())
    val weekFlow: MutableStateFlow<List<ScheduleItem>> = MutableStateFlow(emptyList())
    val subjectsFlow: MutableStateFlow<List<Subject>> = MutableStateFlow(emptyList())

    var getByIdResult: DomainResult<ScheduleItem> = DomainResult.Success(ScheduleFixtures.scheduleItem())
    var getUpcomingResult: DomainResult<ScheduleItem?> =
        DomainResult.Success(ScheduleFixtures.scheduleItem())
    var upsertResultOverride: DomainResult<ScheduleItem>? = null
    var deleteResult: DomainResult<Unit> = DomainResult.Success(Unit)
    var refreshResult: DomainResult<Unit> = DomainResult.Success(Unit)
    var importResult: DomainResult<Int> = DomainResult.Success(3)

    var dayCallCount: Int = 0
    var weekCallCount: Int = 0
    var subjectsCallCount: Int = 0
    var getByIdCallCount: Int = 0
    var getUpcomingCallCount: Int = 0
    var upsertCallCount: Int = 0
    var deleteCallCount: Int = 0
    var refreshCallCount: Int = 0
    var importCallCount: Int = 0

    var lastDayOwnerUserId: String? = null
    var lastDayDate: LocalDate? = null
    var lastDayFilter: ScheduleFilter? = null

    var lastWeekOwnerUserId: String? = null
    var lastWeekStart: LocalDate? = null
    var lastWeekFilter: ScheduleFilter? = null

    var lastSubjectsOwnerUserId: String? = null
    var lastGetById: String? = null
    var lastUpcomingOwnerUserId: String? = null
    var lastUpcomingNow: Instant? = null
    var lastUpsertItem: ScheduleItem? = null
    var lastDeleteId: String? = null
    var lastRefreshOwnerUserId: String? = null
    var lastImportOwnerUserId: String? = null

    override fun observeDay(
        ownerUserId: String,
        date: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> {
        dayCallCount += 1
        lastDayOwnerUserId = ownerUserId
        lastDayDate = date
        lastDayFilter = filter
        return dayFlow
    }

    override fun observeWeek(
        ownerUserId: String,
        weekStart: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> {
        weekCallCount += 1
        lastWeekOwnerUserId = ownerUserId
        lastWeekStart = weekStart
        lastWeekFilter = filter
        return weekFlow
    }

    override fun observeSubjects(ownerUserId: String): Flow<List<Subject>> {
        subjectsCallCount += 1
        lastSubjectsOwnerUserId = ownerUserId
        return subjectsFlow
    }

    override suspend fun getById(id: String): DomainResult<ScheduleItem> {
        getByIdCallCount += 1
        lastGetById = id
        return getByIdResult
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        now: Instant,
    ): DomainResult<ScheduleItem?> {
        getUpcomingCallCount += 1
        lastUpcomingOwnerUserId = ownerUserId
        lastUpcomingNow = now
        return getUpcomingResult
    }

    override suspend fun upsert(item: ScheduleItem): DomainResult<ScheduleItem> {
        upsertCallCount += 1
        lastUpsertItem = item
        return upsertResultOverride ?: DomainResult.Success(item)
    }

    override suspend fun delete(id: String): DomainResult<Unit> {
        deleteCallCount += 1
        lastDeleteId = id
        return deleteResult
    }

    override suspend fun refresh(ownerUserId: String): DomainResult<Unit> {
        refreshCallCount += 1
        lastRefreshOwnerUserId = ownerUserId
        return refreshResult
    }

    override suspend fun importFromUniversity(ownerUserId: String): DomainResult<Int> {
        importCallCount += 1
        lastImportOwnerUserId = ownerUserId
        return importResult
    }
}
