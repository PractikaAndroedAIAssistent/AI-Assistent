package ru.studentai.tests.auth

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
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
import ru.studentai.feature.auth.domain.usecase.LogoutUseCase
import ru.studentai.feature.auth.domain.usecase.UpdateProfileUseCase
import ru.studentai.feature.auth.presentation.profile.ProfileDraft
import ru.studentai.feature.auth.presentation.profile.ProfileEffect
import ru.studentai.feature.auth.presentation.profile.ProfileEvent
import ru.studentai.feature.auth.presentation.profile.ProfileViewModel
import ru.studentai.tests.auth.support.AuthFixtures
import ru.studentai.tests.auth.support.FakeAuthRepository
import ru.studentai.tests.auth.support.TestDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

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
    fun `init loads current profile into success content`() = runTest(dispatcher) {
        val profile = AuthFixtures.profile()
        val repository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(profile)
        }

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.content).isEqualTo(ContentState.Success(profile))
        assertThat(repository.currentProfileCallCount).isEqualTo(1)
    }

    @Test
    fun `edit clicked populates draft from loaded profile`() = runTest(dispatcher) {
        val profile = AuthFixtures.profile(
            university = "BMSTU",
            group = "IU8-11",
            course = 4,
            speciality = "Robotics",
        )
        val repository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(profile)
        }

        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        viewModel.dispatch(ProfileEvent.EditClicked)

        assertThat(viewModel.state.value.isEditing).isEqualTo(true)
        assertThat(viewModel.state.value.draft).isEqualTo(
            ProfileDraft(
                fullName = profile.user.fullName,
                university = "BMSTU",
                group = "IU8-11",
                course = "4",
                speciality = "Robotics",
            ),
        )
    }

    @Test
    fun `save with invalid course emits error and skips update`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        viewModel.dispatch(ProfileEvent.EditClicked)
        viewModel.dispatch(ProfileEvent.CourseChanged("8"))

        viewModel.effects.test {
            viewModel.dispatch(ProfileEvent.SaveClicked)
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(ProfileEffect.ShowError::class)
            assertThat((effect as ProfileEffect.ShowError).message).contains("1")
            assertThat(effect.message).contains("6")
        }

        assertThat(repository.updateProfileCallCount).isEqualTo(0)
        assertThat(viewModel.state.value.isSaving).isFalse()
    }

    @Test
    fun `save success trims draft updates content and emits toast`() = runTest(dispatcher) {
        val savedProfile = AuthFixtures.profile(
            user = AuthFixtures.user(fullName = "Updated Name"),
            university = "MSU",
            group = null,
            course = 5,
            speciality = null,
        )
        val repository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(AuthFixtures.profile())
            updateProfileResult = DomainResult.Success(savedProfile)
        }

        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        viewModel.dispatch(ProfileEvent.EditClicked)
        viewModel.dispatch(ProfileEvent.FullNameChanged("  Updated Name  "))
        viewModel.dispatch(ProfileEvent.UniversityChanged("  MSU  "))
        viewModel.dispatch(ProfileEvent.GroupChanged("   "))
        viewModel.dispatch(ProfileEvent.CourseChanged("5"))
        viewModel.dispatch(ProfileEvent.SpecialityChanged("   "))

        viewModel.effects.test {
            viewModel.dispatch(ProfileEvent.SaveClicked)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(ProfileEffect.ShowSavedToast)
        }

        assertThat(repository.lastUpdatedProfile).isEqualTo(savedProfile)
        assertThat(viewModel.state.value.content).isEqualTo(ContentState.Success(savedProfile))
        assertThat(viewModel.state.value.isEditing).isFalse()
        assertThat(viewModel.state.value.isSaving).isFalse()
    }

    @Test
    fun `logout emits logged out even when repository returns failure`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            logoutResult = DomainResult.Failure(AuthException.RefreshFailed())
        }
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatch(ProfileEvent.LogoutClicked)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(ProfileEffect.LoggedOut)
        }

        assertThat(repository.logoutCallCount).isEqualTo(1)
        assertThat(viewModel.state.value.isLoggingOut).isFalse()
    }

    private fun createViewModel(
        repository: FakeAuthRepository,
        resolver: ErrorMessageResolver = ErrorMessageResolver { "resolved" },
    ): ProfileViewModel = ProfileViewModel(
        getProfileUseCase = GetProfileUseCase(repository),
        updateProfileUseCase = UpdateProfileUseCase(repository),
        logoutUseCase = LogoutUseCase(repository),
        errorResolver = resolver,
        dispatchers = dispatchers,
        logger = NoOpLogger,
    )
}
