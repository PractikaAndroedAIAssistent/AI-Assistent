package ru.studentai.core.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Абстракция над глобальными [kotlinx.coroutines.Dispatchers]. Нужна для тестируемости:
 * в продакшене инжектится [DefaultDispatcherProvider], в тестах — provider на основе
 * `StandardTestDispatcher` / `UnconfinedTestDispatcher`.
 *
 * Все слои выше data **обязаны** брать dispatcher'ы через этот интерфейс,
 * а не из `Dispatchers.*` напрямую.
 */
public interface DispatcherProvider {
    public val main: CoroutineDispatcher
    public val mainImmediate: CoroutineDispatcher
    public val io: CoroutineDispatcher
    public val default: CoroutineDispatcher
    public val unconfined: CoroutineDispatcher
}
