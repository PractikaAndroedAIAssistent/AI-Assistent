package ru.studentai.tests.home

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.home.domain.usecase.GetHomeSnapshotUseCase
import ru.studentai.tests.home.support.FakeAuthRepository
import ru.studentai.tests.home.support.FakeHomeRepository
import ru.studentai.tests.home.support.HomeFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class GetHomeSnapshotUseCaseTest {

    @Test
    fun `student profile delegates to student snapshot loader`() = runTest {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(HomeFixtures.studentProfile())
        }
        val homeRepository = FakeHomeRepository().apply {
            studentResult = DomainResult.Success(HomeFixtures.studentSnapshot(subject = "Math"))
        }

        val result = GetHomeSnapshotUseCase(GetProfileUseCase(authRepository), homeRepository)()

        assertThat(result).isEqualTo(homeRepository.studentResult)
        assertThat(homeRepository.studentCallCount).isEqualTo(1)
        assertThat(homeRepository.teacherCallCount).isEqualTo(0)
        assertThat(homeRepository.lastStudentUser).isEqualTo(HomeFixtures.studentProfile().user)
    }

    @Test
    fun `teacher profile delegates to teacher snapshot loader`() = runTest {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Success(HomeFixtures.teacherProfile())
        }
        val homeRepository = FakeHomeRepository().apply {
            teacherResult = DomainResult.Success(HomeFixtures.teacherSnapshot(subject = "Physics"))
        }

        val result = GetHomeSnapshotUseCase(GetProfileUseCase(authRepository), homeRepository)()

        assertThat(result).isEqualTo(homeRepository.teacherResult)
        assertThat(homeRepository.studentCallCount).isEqualTo(0)
        assertThat(homeRepository.teacherCallCount).isEqualTo(1)
        assertThat(homeRepository.lastTeacherUser).isEqualTo(HomeFixtures.teacherProfile().user)
    }

    @Test
    fun `profile failure is propagated and repository is not called`() = runTest {
        val authRepository = FakeAuthRepository().apply {
            currentProfileResult = DomainResult.Failure(AuthException.Unauthorized())
        }
        val homeRepository = FakeHomeRepository()

        val result = GetHomeSnapshotUseCase(GetProfileUseCase(authRepository), homeRepository)()

        assertThat(result).isEqualTo(authRepository.currentProfileResult)
        assertThat(homeRepository.studentCallCount).isEqualTo(0)
        assertThat(homeRepository.teacherCallCount).isEqualTo(0)
    }
}
