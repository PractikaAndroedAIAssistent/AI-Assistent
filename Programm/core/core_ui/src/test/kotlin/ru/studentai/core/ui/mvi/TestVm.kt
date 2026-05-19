package ru.studentai.core.ui.mvi

import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.NetworkException

internal data class TestState(val count: Int) : UiState

internal sealed interface TestEvent : UiEvent {
    data object Increment : TestEvent
    data class SetValue(val value: Int) : TestEvent
    data object EmitToast : TestEvent
    data object FailWithNetwork : TestEvent
    data object FailWithGeneric : TestEvent
}

internal sealed interface TestEffect : UiEffect {
    data class ShowToast(val text: String) : TestEffect
}

/**
 * Тестовая реализация BaseViewModel для проверки контрактов фреймворка.
 * Не имеет никаких внешних зависимостей кроме DispatcherProvider.
 */
internal class TestVm(
    initial: TestState,
    dispatchers: DispatcherProvider,
    private val onErrorOverride: ((AppException) -> Unit)? = null,
) : BaseViewModel<TestState, TestEvent, TestEffect>(initial, dispatchers) {

    override fun handleEvent(event: TestEvent) {
        when (event) {
            TestEvent.Increment -> updateState { it.copy(count = it.count + 1) }
            is TestEvent.SetValue -> updateState { it.copy(count = event.value) }
            TestEvent.EmitToast -> sendEffect(TestEffect.ShowToast("hello"))
            TestEvent.FailWithNetwork -> launchSafe { throw NetworkException.Timeout() }
            TestEvent.FailWithGeneric -> launchSafe { throw IllegalStateException("boom") }
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        onErrorOverride?.invoke(error)
    }
}
