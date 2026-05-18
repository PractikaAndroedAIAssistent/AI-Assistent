package ru.studentai.core.common.time

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

/**
 * Тестируемая абстракция над текущим временем.
 *
 * Прямое использование `Clock.System.now()` или `LocalDate.now()` в бизнес-логике
 * **запрещено** — это делает тесты недетерминированными.
 * Все use-case'ы, которые работают со временем (дедлайны, расписание, повторение карточек),
 * берут «сейчас» только через этот провайдер.
 */
public interface DateProvider {

    /** Текущая метка времени в UTC. */
    public fun now(): Instant

    /** Текущая дата в указанной таймзоне (по умолчанию — системная). */
    public fun today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate

    /** Текущая дата-время в указанной таймзоне. */
    public fun nowAtZone(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime

    /** Текущая системная таймзона. */
    public fun systemTimeZone(): TimeZone
}
