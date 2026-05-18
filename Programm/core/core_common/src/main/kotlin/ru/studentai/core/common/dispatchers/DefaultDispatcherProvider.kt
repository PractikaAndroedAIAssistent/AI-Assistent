package ru.studentai.core.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Продакшен-реализация [DispatcherProvider] над стандартными [Dispatchers].
 *
 * Регистрируется в Hilt как singleton (см. `core_common` DI-модуль, который появится
 * в Android-обёртке — здесь, в JVM-only модуле, мы не имеем Hilt-зависимости).
 */
public class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
