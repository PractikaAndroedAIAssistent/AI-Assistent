package ru.studentai.tests.home

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.home.domain.model.QuickAction
import ru.studentai.feature.home.domain.usecase.GetHomeSnapshotUseCase
import ru.studentai.feature.home.presentation.home.HomeEffect
import ru.studentai.feature.home.presentation.home.HomeEvent
import ru.studentai.feature.home.presentation.home.HomeViewModel
import ru.studentai.tests.home.support.FakeAuthRepository
import ru.studentai.tests.home.support.FakeHomeRepository
import ru.studentai.tests.home.support.HomeFixtures
import ru.studentai.tests.home.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = TestDispatcherProvider(dispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads snapshot into success state`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(HomeFixtures.studentProfile())
        }
        val homeRepository = FakeHomeRepository().apply {
            studentResult = DomainResult.Success(HomeFixtures.studentSnapshot(subject = "Math"))
        }

        val viewModel = createViewModel(authRepository, homeRepository)

        assertThat(viewModel.state.value.snapshot).isEqualTo(ContentState.Loading)
        advanceUntilIdle()
        assertThat(viewModel.state.value.snapshot).isEqualTo(
            ContentState.Success(HomeFixtures.studentSnapshot(subject = "Math")),
        )
        assertThat(viewModel.state.value.isRefreshing).isFalse()
    }

    @Test
    fun `refresh keeps current snapshot visible and updates refreshing flag`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(HomeFixtures.studentProfile())
        }
        val initialSnapshot = HomeFixtures.studentSnapshot(subject = "Algorithms")
        val refreshedSnapshot = HomeFixtures.studentSnapshot(subject = "Databases")
        val homeRepository = FakeHomeRepository().apply {
            studentResult = DomainResult.Success(initialSnapshot)
        }
        val viewModel = createViewModel(authRepository, homeRepository)
        advanceUntilIdle()

        homeRepository.studentResult = DomainResult.Success(refreshedSnapshot)
        viewModel.dispatch(HomeEvent.Refresh)

        assertThat(viewModel.state.value.snapshot).isEqualTo(ContentState.Success(initialSnapshot))
        assertThat(viewModel.state.value.isRefreshing).isEqualTo(true)
        advanceUntilIdle()
        assertThat(viewModel.state.value.snapshot).isEqualTo(ContentState.Success(refreshedSnapshot))
        assertThat(viewModel.state.value.isRefreshing).isFalse()
    }

    @Test
    fun `failed result updates state to error`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Failure(AuthException.Unauthorized())
        }
        val homeRepository = FakeHomeRepository()

        val viewModel = createViewModel(authRepository, homeRepository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.snapshot).isEqualTo(
            ContentState.Error((authRepository.currentProfileResult as DomainResult.Failure).error),
        )
        assertThat(viewModel.state.value.isRefreshing).isFalse()
    }

    @Test
    fun `profile click emits navigation effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.dispatch(HomeEvent.ProfileClicked)
            assertThat(awaitItem()).isEqualTo(HomeEffect.NavigateToProfile)
        }
    }

    @Test
    fun `quick action click emits navigation effect`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        val action = QuickAction.Student.OpenAi

        viewModel.effects.test {
            viewModel.dispatch(HomeEvent.QuickActionClicked(action))
            assertThat(awaitItem()).isEqualTo(HomeEffect.NavigateQuickAction(action))
        }
    }

    private fun createViewModel(
        authRepository: FakeAuthRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(HomeFixtures.studentProfile())
        },
        homeRepository: FakeHomeRepository = FakeHomeRepository(),
    ): HomeViewModel = HomeViewModel(
        getSnapshot = GetHomeSnapshotUseCase(GetProfileUseCase(authRepository), homeRepository),
        errorResolver = ErrorMessageResolver { "resolved" },
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
