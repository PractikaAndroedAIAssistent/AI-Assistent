package ru.studentai.core.common.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test

class DefaultDateProviderTest {

    private val fixedInstant = Instant.parse("2026-05-18T10:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedInstant
    }
    private val moscowTz = TimeZone.of("Europe/Moscow")

    @Test
    fun `now returns clock instant`() {
        val sut = DefaultDateProvider(fixedClock)
        assertThat(sut.now()).isEqualTo(fixedInstant)
    }

    @Test
    fun `today returns date in given timezone`() {
        val sut = DefaultDateProvider(fixedClock)
        val expected: LocalDate = fixedInstant.toLocalDateTime(moscowTz).date
        assertThat(sut.today(moscowTz)).isEqualTo(expected)
    }

    @Test
    fun `nowAtZone returns LocalDateTime in given timezone`() {
        val sut = DefaultDateProvider(fixedClock)
        val expected: LocalDateTime = fixedInstant.toLocalDateTime(moscowTz)
        assertThat(sut.nowAtZone(moscowTz)).isEqualTo(expected)
    }

    @Test
    fun `today uses system zone by default`() {
        val sut = DefaultDateProvider(fixedClock)
        val expected = fixedInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        assertThat(sut.today()).isEqualTo(expected)
    }
}
