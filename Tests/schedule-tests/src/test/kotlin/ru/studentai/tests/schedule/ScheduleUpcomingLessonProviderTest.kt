package ru.studentai.tests.schedule

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.schedule.domain.model.LessonType
import ru.studentai.feature.schedule.integration.home.ScheduleUpcomingLessonProvider
import ru.studentai.tests.schedule.support.FakeScheduleRepository
import ru.studentai.tests.schedule.support.ScheduleFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleUpcomingLessonProviderTest {

    @Test
    fun `fetch maps repository lesson to upcoming lesson`() = runTest {
        val lesson = ScheduleFixtures.lesson(
            id = "lesson-1",
            lessonType = LessonType.Lecture,
            customTypeLabel = null,
            subjectName = "Algorithms",
            room = "305",
            teacher = "P. Petrov",
        )
        val repository = FakeScheduleRepository().apply {
            getUpcomingResult = DomainResult.Success(lesson)
        }

        val result = ScheduleUpcomingLessonProvider(repository).fetch("student-1")

        assertThat(result).isNotNull()
        assertThat(result?.startAt).isEqualTo(lesson.startAt)
        assertThat(result?.endAt).isEqualTo(lesson.endAt)
        assertThat(result?.subject).isEqualTo("Algorithms")
        assertThat(result?.room).isEqualTo("305")
        assertThat(result?.teacher).isEqualTo("P. Petrov")
        assertThat(result?.lessonType).isNotEqualTo(LessonType.Lecture.name)
        assertThat(repository.getUpcomingCallCount).isEqualTo(1)
        assertThat(repository.lastUpcomingOwnerUserId).isEqualTo("student-1")
        assertThat(repository.lastUpcomingNow).isNotNull()
    }

    @Test
    fun `fetch prefers custom type label over default lesson type label`() = runTest {
        val repository = FakeScheduleRepository().apply {
            getUpcomingResult = DomainResult.Success(
                ScheduleFixtures.lesson(
                    lessonType = LessonType.Other,
                    customTypeLabel = "Workshop",
                ),
            )
        }

        val result = ScheduleUpcomingLessonProvider(repository).fetch("student-1")

        assertThat(result?.lessonType).isEqualTo("Workshop")
    }

    @Test
    fun `fetch returns null when repository returns failure`() = runTest {
        val repository = FakeScheduleRepository().apply {
            getUpcomingResult = DomainResult.Failure(StorageException.Io())
        }

        val result = ScheduleUpcomingLessonProvider(repository).fetch("student-1")

        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `fetch returns null when repository has no upcoming lesson`() = runTest {
        val repository = FakeScheduleRepository().apply {
            getUpcomingResult = DomainResult.Success(null)
        }

        val result = ScheduleUpcomingLessonProvider(repository).fetch("student-1")

        assertThat(result).isEqualTo(null)
    }
}
