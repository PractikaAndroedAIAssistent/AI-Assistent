package ru.studentai.core.network.interceptor

/**
 * Флаг детального HTTP-логирования.
 *
 * Реализуется в app-модуле — обычно возвращает `BuildConfig.DEBUG`.
 * core_network не знает про `BuildConfig`, поэтому абстракция вынесена сюда.
 */
public fun interface NetworkLoggingFlag {
    public fun isEnabled(): Boolean
}

/** Дефолт — логирование выключено (release-friendly fallback). */
public class DisabledNetworkLoggingFlag : NetworkLoggingFlag {
    override fun isEnabled(): Boolean = false
}
