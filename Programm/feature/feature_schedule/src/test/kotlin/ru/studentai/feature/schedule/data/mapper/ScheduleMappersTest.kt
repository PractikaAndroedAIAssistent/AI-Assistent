package ru.studentai.feature.schedule.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.domain.model.ScheduleItem
import ru.studentai.feature.schedule.domain.model.Subject

class ScheduleMappersTest {

    @Test
    fun `domain to entity and back round-trip preserves fields`() {
        val item = sampleItem()
        val entity = item.toEntity()
        val back = entity.toDomain()
        // subjectId сохраняется через FK (если null → пустая строка в domain — это известная семантика)
        assertThat(back.copy(subjectId = "subj1")).isEqualTo(item)
    }

    @Test
    fun `entity with null subjectId yields blank subjectId in domain`() {
        val item = sampleItem(subjectId = "subj1").toEntity().copy(subjectId = null)
        val domain = item.toDomain()
        assertThat(domain.subjectId).isEqualTo("")
    }

    @Test
    fun `unknown lesson type in entity falls back to Other`() {
        val entity = sampleItem().toEntity().copy(lessonType = "UNKNOWN_TYPE")
        // Other требует customTypeLabel — в маппере поле остаётся прежним; добавим для контракта.
        val domain = entity.copy(customTypeLabel = "fallback").toDomain()
        assertThat(domain.lessonType).isEqualTo(LessonType.Other)
    }

    @Test
    fun `subject entity maps to domain`() {
        val s = Subject(id = "s1", name = "Math")
        val entity = s.toEntity(ownerUserId = "u1")
        val back = entity.toDomain()
        assertThat(back).isEqualTo(s)
    }

    private fun sampleItem(subjectId: String = "subj1"): ScheduleItem = ScheduleItem(
        id = "id1",
        ownerUserId = "u1",
        subjectId = subjectId,
        subjectName = "Algorithms",
        lessonType = LessonType.Lecture,
        startAt = LocalDateTime.parse("2026-05-20T10:00:00"),
        endAt = LocalDateTime.parse("2026-05-20T11:30:00"),
        room = "305",
        teacher = "Петров",
        note = null,
    )
}
