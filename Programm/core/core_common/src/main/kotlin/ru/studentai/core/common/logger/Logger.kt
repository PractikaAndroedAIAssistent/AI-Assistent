package ru.studentai.core.common.logger

/**
 * Платформонезависимый интерфейс логирования.
 *
 * `core_common` — Kotlin-JVM модуль, без `android.util.Log` зависимости.
 * Android-реализация (например, через Timber) живёт выше — в Android-модулях.
 * Бизнес-логика и domain-слой логируют только через этот интерфейс.
 */
public interface Logger {

    public fun verbose(tag: String, message: String, throwable: Throwable? = null)
    public fun debug(tag: String, message: String, throwable: Throwable? = null)
    public fun info(tag: String, message: String, throwable: Throwable? = null)
    public fun warn(tag: String, message: String, throwable: Throwable? = null)
    public fun error(tag: String, message: String, throwable: Throwable? = null)

    public companion object {
        public const val DEFAULT_TAG: String = "StudentAI"
    }
}

/** Удобное расширение для логирования с класс-тегом. */
public inline fun <reified T> Logger.taggedFor(): TaggedLogger =
    TaggedLogger(this, T::class.simpleName ?: Logger.DEFAULT_TAG)

/** Lightweight-обёртка с предзаданным тегом. */
public class TaggedLogger(
    private val delegate: Logger,
    private val tag: String,
) {
    public fun verbose(message: String, throwable: Throwable? = null): Unit =
        delegate.verbose(tag, message, throwable)

    public fun debug(message: String, throwable: Throwable? = null): Unit =
        delegate.debug(tag, message, throwable)

    public fun info(message: String, throwable: Throwable? = null): Unit =
        delegate.info(tag, message, throwable)

    public fun warn(message: String, throwable: Throwable? = null): Unit =
        delegate.warn(tag, message, throwable)

    public fun error(message: String, throwable: Throwable? = null): Unit =
        delegate.error(tag, message, throwable)
}
