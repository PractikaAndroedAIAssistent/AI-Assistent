package ru.studentai.tests.auth

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
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
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.usecase.RegisterUseCase
import ru.studentai.feature.auth.domain.usecase.ValidateRegisterInputUseCase
import ru.studentai.feature.auth.presentation.register.RegisterEffect
import ru.studentai.feature.auth.presentation.register.RegisterEvent
import ru.studentai.feature.auth.presentation.register.RegisterViewModel
import ru.studentai.tests.auth.support.AuthFixtures
import ru.studentai.tests.auth.support.FakeAuthRepository
import ru.studentai.tests.auth.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

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
    fun `submit with invalid fields exposes validation errors and skips register`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.dispatch(RegisterEvent.FullNameChanged(""))
        viewModel.dispatch(RegisterEvent.EmailChanged("bad-email"))
        viewModel.dispatch(RegisterEvent.PasswordChanged("short"))
        viewModel.dispatch(RegisterEvent.PasswordRepeatChanged("different"))
        viewModel.dispatch(RegisterEvent.SubmitClicked)
        advanceUntilIdle()

        assertThat(viewModel.state.value.fullNameError).isNotNull()
        assertThat(viewModel.state.value.emailError).isNotNull()
        assertThat(viewModel.state.value.passwordError).isNotNull()
        assertThat(viewModel.state.value.passwordRepeatError).isEqualTo("Passwords do not match")
        assertThat(viewModel.state.value.isSubmitting).isFalse()
        assertThat(repository.registerCallCount).isEqualTo(0)
    }

    @Test
    fun `submit with invalid course shows message and skips register`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        fillValidForm(viewModel)
        viewModel.dispatch(RegisterEvent.CourseChanged("7"))
        viewModel.dispatch(RegisterEvent.SubmitClicked)
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isNotNull()
        assertThat(viewModel.state.value.errorMessage!!).contains("1")
        assertThat(viewModel.state.value.errorMessage!!).contains("6")
        assertThat(repository.registerCallCount).isEqualTo(0)
    }

    @Test
    fun `submit success trims optional fields and emits success effects`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            registerResult = DomainResult.Success(AuthFixtures.user())
        }
        val viewModel = createViewModel(repository)

        viewModel.effects.test {
            viewModel.dispatch(RegisterEvent.FullNameChanged("  Ivan Petrov  "))
            viewModel.dispatch(RegisterEvent.EmailChanged("  student@example.com  "))
            viewModel.dispatch(RegisterEvent.PasswordChanged("Abcdef12"))
            viewModel.dispatch(RegisterEvent.PasswordRepeatChanged("Abcdef12"))
            viewModel.dispatch(RegisterEvent.RoleSelected(UserRole.Student))
            viewModel.dispatch(RegisterEvent.UniversityChanged("  MSU  "))
            viewModel.dispatch(RegisterEvent.GroupChanged("   "))
            viewModel.dispatch(RegisterEvent.CourseChanged("2"))
            viewModel.dispatch(RegisterEvent.SpecialityChanged("   "))
            viewModel.dispatch(RegisterEvent.SubmitClicked)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(RegisterEffect.ShowRegisteredToast)
            assertThat(awaitItem()).isEqualTo(RegisterEffect.NavigateHome)
        }

        assertThat(repository.lastRegistrationData).isEqualTo(
            RegistrationData(
                fullName = "Ivan Petrov",
                email = "student@example.com",
                password = "Abcdef12",
                role = UserRole.Student,
                university = "MSU",
                group = null,
                course = 2,
                speciality = null,
            ),
        )
        assertThat(viewModel.state.value.isSubmitting).isFalse()
    }

    @Test
    fun `submit failure resolves message and emits error effect`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            registerResult = DomainResult.Failure(AuthException.EmailAlreadyTaken())
        }
        val resolver: ErrorMessageResolver = mockk {
            every { resolve(any()) } returns "Email is already taken"
        }
        val viewModel = createViewModel(repository, resolver)

        viewModel.effects.test {
            fillValidForm(viewModel)
            viewModel.dispatch(RegisterEvent.SubmitClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(RegisterEffect.ShowError::class)
            assertThat((effect as RegisterEffect.ShowError).message).isEqualTo("Email is already taken")
        }

        assertThat(viewModel.state.value.errorMessage).isEqualTo("Email is already taken")
        assertThat(viewModel.state.value.isSubmitting).isFalse()
    }

    private fun createViewModel(
        repository: FakeAuthRepository,
        resolver: ErrorMessageResolver = ErrorMessageResolver { "resolved" },
    ): RegisterViewModel = RegisterViewModel(
        registerUseCase = RegisterUseCase(repository),
        validateInput = ValidateRegisterInputUseCase(),
        errorResolver = resolver,
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )

    private fun fillValidForm(viewModel: RegisterViewModel) {
        viewModel.dispatch(RegisterEvent.FullNameChanged("Ivan Petrov"))
        viewModel.dispatch(RegisterEvent.EmailChanged("student@example.com"))
        viewModel.dispatch(RegisterEvent.PasswordChanged("Abcdef12"))
        viewModel.dispatch(RegisterEvent.PasswordRepeatChanged("Abcdef12"))
    }
}
