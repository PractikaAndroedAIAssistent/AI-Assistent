package ru.studentai.core.ui.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import ru.studentai.core.common.dispatchers.DispatcherProvider

/**
 * [DispatcherProvider] для unit-тестов: все четыре dispatcher'а возвращают
 * один и тот же [TestDispatcher] (`StandardTestDispatcher` или `UnconfinedTestDispatcher`).
 *
 * Это упрощает контроль времени в тестах: `advanceUntilIdle()` управляет всем.
 */
internal class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher,
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val mainImmediate: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}
