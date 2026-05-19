package ru.studentai.core.ui.mvi

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.NetworkException

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = TestDispatcherProvider(testDispatcher)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is exposed on state flow`() = runTest(testDispatcher) {
        val vm = TestVm(initial = TestState(count = 0), dispatchers = dispatchers)
        vm.state.test {
            assertThat(awaitItem()).isEqualTo(TestState(count = 0))
        }
    }

    @Test
    fun `dispatch Increment updates state`() = runTest(testDispatcher) {
        val vm = TestVm(initial = TestState(0), dispatchers = dispatchers)
        vm.state.test {
            assertThat(awaitItem().count).isEqualTo(0)
            vm.dispatch(TestEvent.Increment)
            assertThat(awaitItem().count).isEqualTo(1)
        }
    }

    @Test
    fun `dispatch SetValue replaces count`() = runTest(testDispatcher) {
        val vm = TestVm(initial = TestState(0), dispatchers = dispatchers)
        vm.dispatch(TestEvent.SetValue(42))
        advanceUntilIdle()
        assertThat(vm.state.value.count).isEqualTo(42)
    }

    @Test
    fun `effect is delivered through effects flow`() = runTest(testDispatcher) {
        val vm = TestVm(initial = TestState(0), dispatchers = dispatchers)
        vm.effects.test {
            vm.dispatch(TestEvent.EmitToast)
            assertThat(awaitItem()).isEqualTo(TestEffect.ShowToast("hello"))
        }
    }

    @Test
    fun `launchSafe catches AppException and routes to handler`() = runTest(testDispatcher) {
        val captured = mutableListOf<AppException>()
        val vm = TestVm(
            initial = TestState(0),
            dispatchers = dispatchers,
            onErrorOverride = { captured += it },
        )
        vm.dispatch(TestEvent.FailWithNetwork)
        advanceUntilIdle()
        assertThat(captured).hasSize(1)
        assertThat(captured.single()).isInstanceOf(NetworkException.Timeout::class)
    }

    @Test
    fun `launchSafe wraps unknown throwable as UnknownException for handler`() = runTest(testDispatcher) {
        val captured = mutableListOf<AppException>()
        val vm = TestVm(
            initial = TestState(0),
            dispatchers = dispatchers,
            onErrorOverride = { captured += it },
        )
        vm.dispatch(TestEvent.FailWithGeneric)
        advanceUntilIdle()
        assertThat(captured.single()).isInstanceOf(ru.studentai.core.common.error.UnknownException::class)
    }

    @Test
    fun `state retains last value after multiple updates`() = runTest(testDispatcher) {
        val vm = TestVm(initial = TestState(0), dispatchers = dispatchers)
        vm.dispatch(TestEvent.Increment)
        vm.dispatch(TestEvent.Increment)
        vm.dispatch(TestEvent.Increment)
        advanceUntilIdle()
        assertThat(vm.state.value.count).isEqualTo(3)
    }
}
