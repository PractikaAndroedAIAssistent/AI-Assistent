package ru.studentai.core.common.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Продакшен-реализация [DateProvider] над системными часами.
 *
 * Принимает [Clock] параметром — это позволяет в тестах подменить часы,
 * не создавая отдельный класс-двойник.
 */
public class DefaultDateProvider(
    private val clock: Clock = Clock.System,
) : DateProvider {

    override fun now(): Instant = clock.now()

    override fun today(timeZone: TimeZone): LocalDate =
        clock.now().toLocalDateTime(timeZone).date

    override fun nowAtZone(timeZone: TimeZone): LocalDateTime =
        clock.now().toLocalDateTime(timeZone)

    override fun systemTimeZone(): TimeZone = TimeZone.currentSystemDefault()
}
