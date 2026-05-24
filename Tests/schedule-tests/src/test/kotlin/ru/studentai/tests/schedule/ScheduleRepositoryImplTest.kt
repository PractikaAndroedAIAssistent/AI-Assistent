package ru.studentai.tests.schedule

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.error.HttpErrorMapper
import ru.studentai.feature.schedule.data.local.dao.ScheduleDao
import ru.studentai.feature.schedule.data.local.dao.SubjectDao
import ru.studentai.feature.schedule.data.local.entity.ScheduleItemEntity
import ru.studentai.feature.schedule.data.local.entity.SubjectEntity
import ru.studentai.feature.schedule.data.remote.api.ScheduleApi
import ru.studentai.feature.schedule.data.remote.dto.UpsertScheduleItemRequest
import ru.studentai.feature.schedule.data.repository.ScheduleRepositoryImpl
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.tests.schedule.support.ScheduleFixtures
import ru.studentai.tests.schedule.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestDispatcherProvider(dispatcher)

    private val scheduleDao: ScheduleDao = mockk(relaxed = true)
    private val subjectDao: SubjectDao = mockk(relaxed = true)
    private val api: ScheduleApi = mockk(relaxed = true)

    private val sut = ScheduleRepositoryImpl(
        scheduleDao = scheduleDao,
        subjectDao = subjectDao,
        api = api,
        errorMapper = HttpErrorMapper(),
        dispatchers = dispatchers,
    )

    @Test
    fun `observe day uses selected date bounds and subject filter`() = runTest(dispatcher) {
        val ownerUserId = "student-1"
        val date = LocalDate.parse("2026-05-20")
        val filter = ScheduleFilter(subjectId = "subject-2")
        val entity = ScheduleFixtures.scheduleItemEntity(
            ownerUserId = ownerUserId,
            subjectId = "subject-2",
            startAt = date.atTime(LocalTime(9, 0)),
            endAt = date.atTime(LocalTime(10, 30)),
        )
        val ownerSlot = slot<String>()
        val fromSlot = slot<LocalDateTime>()
        val untilSlot = slot<LocalDateTime>()

        every {
            scheduleDao.observeBetween(
                ownerUserId = capture(ownerSlot),
                from = capture(fromSlot),
                until = capture(untilSlot),
                subjectId = "subject-2",
            )
        } returns flowOf(listOf(entity))

        val result = sut.observeDay(ownerUserId, date, filter).first()

        assertThat(ownerSlot.captured).isEqualTo(ownerUserId)
        assertThat(fromSlot.captured).isEqualTo(date.atTime(LocalTime(0, 0)))
        assertThat(untilSlot.captured).isEqualTo(date.plus(DatePeriod(days = 1)).atTime(LocalTime(0, 0)))
        assertThat(result).isEqualTo(
            listOf(
                ScheduleFixtures.scheduleItem(
                    ownerUserId = ownerUserId,
                    subjectId = "subject-2",
                    startAt = date.atTime(LocalTime(9, 0)),
                    endAt = date.atTime(LocalTime(10, 30)),
                ),
            ),
        )
    }

    @Test
    fun `observe week normalizes anchor date to monday boundaries`() = runTest(dispatcher) {
        val ownerUserId = "student-1"
        val anchorDate = LocalDate.parse("2026-05-20")
        val monday = LocalDate.parse("2026-05-18")
        val ownerSlot = slot<String>()
        val fromSlot = slot<LocalDateTime>()
        val untilSlot = slot<LocalDateTime>()

        every {
            scheduleDao.observeBetween(
                ownerUserId = capture(ownerSlot),
                from = capture(fromSlot),
                until = capture(untilSlot),
                subjectId = null,
            )
        } returns flowOf(emptyList())

        sut.observeWeek(ownerUserId, anchorDate, ScheduleFilter.NONE).first()

        assertThat(ownerSlot.captured).isEqualTo(ownerUserId)
        assertThat(fromSlot.captured).isEqualTo(monday.atTime(LocalTime(0, 0)))
        assertThat(untilSlot.captured).isEqualTo(monday.plus(DatePeriod(days = 7)).atTime(LocalTime(0, 0)))
    }

    @Test
    fun `getById returns not found failure when dao has no item`() = runTest(dispatcher) {
        coEvery { scheduleDao.getById("missing") } returns null

        val result = sut.getById("missing")

        assertThat(result).isInstanceOf(DomainResult.Failure::class)
        assertThat((result as DomainResult.Failure).error).isInstanceOf(StorageException.NotFound::class)
    }

    @Test
    fun `upsert with placeholder id calls create and caches server response`() = runTest(dispatcher) {
        val item = ScheduleFixtures.scheduleItem(
            id = ScheduleRepositoryImpl.NEW_ID_PLACEHOLDER,
            ownerUserId = "student-1",
            subjectId = "subject-1",
            subjectName = "Discrete Math",
        )
        val response = ScheduleFixtures.scheduleItemDto(
            id = "server-lesson-1",
            subjectId = "subject-1",
            subjectName = "Discrete Math",
            note = "Saved on backend",
        )
        val requestSlot = slot<UpsertScheduleItemRequest>()
        val entitySlot = slot<ScheduleItemEntity>()

        coEvery { api.create(capture(requestSlot)) } returns response
        coEvery { scheduleDao.upsert(capture(entitySlot)) } returns Unit

        val result = sut.upsert(item)

        assertThat(result).isEqualTo(
            DomainResult.Success(ScheduleFixtures.scheduleItemFromDto(response, ownerUserId = item.ownerUserId)),
        )
        assertThat(requestSlot.captured.subjectId).isEqualTo("subject-1")
        assertThat(requestSlot.captured.subjectName).isEqualTo("Discrete Math")
        assertThat(entitySlot.captured.id).isEqualTo("server-lesson-1")
        assertThat(entitySlot.captured.note).isEqualTo("Saved on backend")
        coVerify(exactly = 1) { api.create(any()) }
        coVerify(exactly = 0) { api.update(any(), any()) }
    }

    @Test
    fun `upsert with existing id calls update and caches server response`() = runTest(dispatcher) {
        val item = ScheduleFixtures.scheduleItem(
            id = "lesson-42",
            ownerUserId = "student-1",
            subjectName = "Physics",
            note = "Client copy",
        )
        val response = ScheduleFixtures.scheduleItemDto(
            id = "lesson-42",
            subjectName = "Physics",
            note = "Updated on backend",
        )
        val requestSlot = slot<UpsertScheduleItemRequest>()
        val entitySlot = slot<ScheduleItemEntity>()

        coEvery { api.update("lesson-42", capture(requestSlot)) } returns response
        coEvery { scheduleDao.upsert(capture(entitySlot)) } returns Unit

        val result = sut.upsert(item)

        assertThat(result).isEqualTo(
            DomainResult.Success(ScheduleFixtures.scheduleItemFromDto(response, ownerUserId = item.ownerUserId)),
        )
        assertThat(requestSlot.captured.note).isEqualTo("Client copy")
        assertThat(entitySlot.captured.note).isEqualTo("Updated on backend")
        coVerify(exactly = 0) { api.create(any()) }
        coVerify(exactly = 1) { api.update("lesson-42", any()) }
    }

    @Test
    fun `refresh stores synced subjects and items for owner`() = runTest(dispatcher) {
        val ownerUserId = "student-7"
        val response = ScheduleFixtures.syncResponse(
            subjects = listOf(ScheduleFixtures.subjectDto(id = "subject-9", name = "Databases")),
            items = listOf(
                ScheduleFixtures.scheduleItemDto(
                    id = "lesson-9",
                    subjectId = "subject-9",
                    subjectName = "Databases",
                ),
            ),
        )
        val subjectsSlot = slot<List<SubjectEntity>>()
        val itemsSlot = slot<List<ScheduleItemEntity>>()

        coEvery { api.sync() } returns response
        coEvery { subjectDao.upsertAll(capture(subjectsSlot)) } returns Unit
        coEvery { scheduleDao.upsertAll(capture(itemsSlot)) } returns Unit

        val result = sut.refresh(ownerUserId)

        assertThat(result).isEqualTo(DomainResult.Success(Unit))
        assertThat(subjectsSlot.captured).isEqualTo(
            listOf(ScheduleFixtures.subjectEntity(id = "subject-9", ownerUserId = ownerUserId, name = "Databases")),
        )
        assertThat(itemsSlot.captured).isEqualTo(
            listOf(
                ScheduleFixtures.scheduleItemEntity(
                    id = "lesson-9",
                    ownerUserId = ownerUserId,
                    subjectId = "subject-9",
                    subjectName = "Databases",
                ),
            ),
        )
    }

    @Test
    fun `import from university syncs cache and returns imported count`() = runTest(dispatcher) {
        val ownerUserId = "student-3"
        val importResponse = ScheduleFixtures.importResponse(importedCount = 4)
        val syncResponse = ScheduleFixtures.syncResponse(
            subjects = listOf(ScheduleFixtures.subjectDto(id = "subject-5", name = "Networks")),
            items = listOf(
                ScheduleFixtures.scheduleItemDto(
                    id = "lesson-5",
                    subjectId = "subject-5",
                    subjectName = "Networks",
                ),
            ),
        )
        val subjectsSlot = slot<List<SubjectEntity>>()
        val itemsSlot = slot<List<ScheduleItemEntity>>()

        coEvery { api.importFromUniversity() } returns importResponse
        coEvery { api.sync() } returns syncResponse
        coEvery { subjectDao.upsertAll(capture(subjectsSlot)) } returns Unit
        coEvery { scheduleDao.upsertAll(capture(itemsSlot)) } returns Unit

        val result = sut.importFromUniversity(ownerUserId)

        assertThat(result).isEqualTo(DomainResult.Success(4))
        assertThat(subjectsSlot.captured.single().ownerUserId).isEqualTo(ownerUserId)
        assertThat(itemsSlot.captured.single().ownerUserId).isEqualTo(ownerUserId)
        coVerify(exactly = 1) { api.importFromUniversity() }
        coVerify(exactly = 1) { api.sync() }
    }
}
