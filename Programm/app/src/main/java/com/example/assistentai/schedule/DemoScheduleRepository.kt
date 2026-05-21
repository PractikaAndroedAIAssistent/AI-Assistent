package com.example.assistentai.schedule

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleFilter
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository

/**
 * Demo-реализация [ScheduleRepository] для запуска без backend.
 *
 * Хранит данные in-memory в [MutableStateFlow]'ах. Запитывается двумя предметами
 * и расписанием на текущую и следующую неделю — этого достаточно, чтобы карточка
 * «Ближайшая пара» на главной получала реальные данные, и можно было прокликать
 * день/неделю + создание/редактирование/удаление.
 *
 * При появлении реального backend этот биндинг заменяется на [ru.studentai.feature.schedule.data.repository.ScheduleRepositoryImpl].
 */
@Singleton
public class DemoScheduleRepository @Inject constructor() : ScheduleRepository {

    private val subjects: MutableStateFlow<List<Subject>> = MutableStateFlow(initialSubjects())
    private val items: MutableStateFlow<List<ScheduleItem>> = MutableStateFlow(emptyList())

    init {
        // Сидируем расписание относительно текущей даты.
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        items.value = seedItems(today)
    }

    override fun observeDay(
        ownerUserId: String,
        date: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> = items.map { all ->
        all.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .filter { it.startAt.date == date }
            .filter { filter.subjectId == null || it.subjectId == filter.subjectId }
            .sortedBy { it.startAt }
    }

    override fun observeWeek(
        ownerUserId: String,
        weekStart: LocalDate,
        filter: ScheduleFilter,
    ): Flow<List<ScheduleItem>> = items.map { all ->
        val weekEnd = weekStart.plus(DatePeriod(days = 7))
        all.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .filter { it.startAt.date in weekStart..weekEnd.minus(DatePeriod(days = 1)) }
            .filter { filter.subjectId == null || it.subjectId == filter.subjectId }
            .sortedBy { it.startAt }
    }

    override fun observeSubjects(ownerUserId: String): Flow<List<Subject>> = subjects

    override suspend fun getById(id: String): DomainResult<ScheduleItem> {
        delay(SIMULATED_DELAY_MS)
        return items.value.firstOrNull { it.id == id }
            ?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(StorageException.NotFound(entity = "ScheduleItem", id = id))
    }

    override suspend fun getUpcoming(
        ownerUserId: String,
        now: Instant,
    ): DomainResult<ScheduleItem?> {
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val upcoming = items.value
            .filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .filter { it.startAt > localNow }
            .minByOrNull { it.startAt }
        return DomainResult.Success(upcoming)
    }

    override suspend fun upsert(item: ScheduleItem): DomainResult<ScheduleItem> {
        delay(SIMULATED_DELAY_MS)
        // Демо: подставляем subjectId по имени; если предмета нет — создаём.
        val resolvedSubjectId = subjects.value.firstOrNull { it.name == item.subjectName }?.id
            ?: addSubject(item.subjectName)
        val toSave = item.copy(subjectId = resolvedSubjectId)
        items.update { list ->
            val existingIndex = list.indexOfFirst { it.id == toSave.id }
            if (existingIndex >= 0) {
                list.toMutableList().apply { set(existingIndex, toSave) }
            } else {
                list + toSave
            }
        }
        return DomainResult.Success(toSave)
    }

    override suspend fun delete(id: String): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS / 2)
        items.update { list -> list.filterNot { it.id == id } }
        return DomainResult.Success(Unit)
    }

    override suspend fun refresh(ownerUserId: String): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS)
        return DomainResult.Success(Unit)
    }

    override suspend fun importFromUniversity(ownerUserId: String): DomainResult<Int> {
        delay(SIMULATED_DELAY_MS * 2)
        // Симулируем импорт: добавляем 2 пары на следующий понедельник
        val nextMonday = nextMonday()
        val mathId = addSubject("Высшая математика")
        val englishId = addSubject("Иностранный язык")
        val imported = listOf(
            ScheduleItem(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = mathId,
                subjectName = "Высшая математика",
                lessonType = LessonType.Lecture,
                startAt = nextMonday.atTime(LocalTime(10, 30)),
                endAt = nextMonday.atTime(LocalTime(12, 0)),
                room = "204",
                teacher = "Сидоров А.А.",
            ),
            ScheduleItem(
                id = UUID.randomUUID().toString(),
                ownerUserId = OWNER_ALL,
                subjectId = englishId,
                subjectName = "Иностранный язык",
                lessonType = LessonType.Seminar,
                startAt = nextMonday.atTime(LocalTime(12, 30)),
                endAt = nextMonday.atTime(LocalTime(14, 0)),
                room = "115",
                teacher = "Иванова Е.К.",
            ),
        )
        items.update { it + imported }
        return DomainResult.Success(imported.size)
    }

    private fun addSubject(name: String): String {
        val existing = subjects.value.firstOrNull { it.name == name }
        if (existing != null) return existing.id
        val id = UUID.randomUUID().toString()
        subjects.update { it + Subject(id = id, name = name) }
        return id
    }

    private fun nextMonday(): LocalDate {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val offset = (DayOfWeek.MONDAY.value - today.dayOfWeek.value + 7) % 7
        return if (offset == 0) today.plus(DatePeriod(days = 7)) else today.plus(DatePeriod(days = offset))
    }

    private fun initialSubjects(): List<Subject> = listOf(
        Subject(id = "demo-algo", name = "Алгоритмы"),
        Subject(id = "demo-os", name = "Операционные системы"),
        Subject(id = "demo-db", name = "Базы данных"),
    )

    private fun seedItems(today: LocalDate): List<ScheduleItem> {
        // Сегодняшние две пары
        val first = ScheduleItem(
            id = "demo-lesson-1",
            ownerUserId = OWNER_ALL,
            subjectId = "demo-algo",
            subjectName = "Алгоритмы",
            lessonType = LessonType.Lecture,
            startAt = today.atTime(LocalTime(10, 30)),
            endAt = today.atTime(LocalTime(12, 0)),
            room = "305",
            teacher = "Петров П.П.",
            note = "Сортировки",
        )
        val second = ScheduleItem(
            id = "demo-lesson-2",
            ownerUserId = OWNER_ALL,
            subjectId = "demo-db",
            subjectName = "Базы данных",
            lessonType = LessonType.Lab,
            startAt = today.atTime(LocalTime(12, 30)),
            endAt = today.atTime(LocalTime(14, 0)),
            room = "411",
            teacher = "Соколова И.В.",
        )
        // Завтрашняя одна пара
        val tomorrow = today.plus(DatePeriod(days = 1))
        val third = ScheduleItem(
            id = "demo-lesson-3",
            ownerUserId = OWNER_ALL,
            subjectId = "demo-os",
            subjectName = "Операционные системы",
            lessonType = LessonType.Seminar,
            startAt = tomorrow.atTime(LocalTime(9, 0)),
            endAt = tomorrow.atTime(LocalTime(10, 30)),
            room = "207",
            teacher = "Кузнецов Д.С.",
        )
        return listOf(first, second, third)
    }

    private companion object {
        /** Маркер «общий для всех демо-пользователей» — упрощает использование без серверного userId. */
        const val OWNER_ALL = "*"
        const val SIMULATED_DELAY_MS = 250L
    }
}
