package ru.studentai.tests.auth

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import io.mockk.every
import io.mockk.mockk
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
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.usecase.LoginUseCase
import ru.studentai.feature.auth.domain.usecase.ValidateLoginInputUseCase
import ru.studentai.feature.auth.presentation.login.LoginEffect
import ru.studentai.feature.auth.presentation.login.LoginEvent
import ru.studentai.feature.auth.presentation.login.LoginViewModel
import ru.studentai.tests.auth.support.FakeAuthRepository
import ru.studentai.tests.auth.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

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
    fun `submit with invalid input populates field errors and skips login`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.dispatch(LoginEvent.EmailChanged("not-an-email"))
        viewModel.dispatch(LoginEvent.PasswordChanged(""))
        viewModel.dispatch(LoginEvent.SubmitClicked)
        advanceUntilIdle()

        assertThat(viewModel.state.value.emailError).isNotNull()
        assertThat(viewModel.state.value.passwordError).isNotNull()
        assertThat(viewModel.state.value.isSubmitting).isFalse()
        assertThat(repository.loginCallCount).isEqualTo(0)
    }

    @Test
    fun `submit trims email and navigates home on success`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            loginResult = DomainResult.Success(ru.studentai.tests.auth.support.AuthFixtures.user())
        }
        val viewModel = createViewModel(repository)

        viewModel.effects.test {
            viewModel.dispatch(LoginEvent.EmailChanged("  student@example.com  "))
            viewModel.dispatch(LoginEvent.PasswordChanged("Abc12345"))
            viewModel.dispatch(LoginEvent.SubmitClicked)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(LoginEffect.NavigateHome)
        }

        assertThat(repository.lastLoginCredentials).isEqualTo(
            AuthCredentials(email = "student@example.com", password = "Abc12345"),
        )
        assertThat(viewModel.state.value.isSubmitting).isFalse()
    }

    @Test
    fun `submit failure resolves message and emits error effect`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            loginResult = DomainResult.Failure(AuthException.InvalidCredentials())
        }
        val resolver: ErrorMessageResolver = mockk {
            every { resolve(any()) } returns "Wrong email or password"
        }
        val viewModel = createViewModel(repository, resolver)

        viewModel.effects.test {
            viewModel.dispatch(LoginEvent.EmailChanged("student@example.com"))
            viewModel.dispatch(LoginEvent.PasswordChanged("Abc12345"))
            viewModel.dispatch(LoginEvent.SubmitClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(LoginEffect.ShowError::class)
            assertThat((effect as LoginEffect.ShowError).message).isEqualTo("Wrong email or password")
        }

        assertThat(viewModel.state.value.errorMessage).isEqualTo("Wrong email or password")
        assertThat(viewModel.state.value.isSubmitting).isFalse()
    }

    private fun createViewModel(
        repository: FakeAuthRepository,
        resolver: ErrorMessageResolver = ErrorMessageResolver { "resolved" },
    ): LoginViewModel = LoginViewModel(
        loginUseCase = LoginUseCase(repository),
        validateInput = ValidateLoginInputUseCase(),
        errorResolver = resolver,
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
