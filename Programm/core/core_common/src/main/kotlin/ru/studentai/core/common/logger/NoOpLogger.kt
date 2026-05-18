package ru.studentai.core.common.logger

/**
 * Дефолтная пустая реализация. Используется:
 *  • как fallback, когда конкретная платформа не задаёт свой Logger;
 *  • в тестах, где логи не нужны;
 *  • как entry-point заглушка во время bootstrap'а до инициализации DI.
 */
public object NoOpLogger : Logger {
    override fun verbose(tag: String, message: String, throwable: Throwable?) { /* no-op */ }
    override fun debug(tag: String, message: String, throwable: Throwable?) { /* no-op */ }
    override fun info(tag: String, message: String, throwable: Throwable?) { /* no-op */ }
    override fun warn(tag: String, message: String, throwable: Throwable?) { /* no-op */ }
    override fun error(tag: String, message: String, throwable: Throwable?) { /* no-op */ }
}
