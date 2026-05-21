package ru.studentai.tests.home.support

import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.repository.HomeRepository

internal class FakeHomeRepository : HomeRepository {

    var studentResult: DomainResult<HomeSnapshot.Student> =
        DomainResult.Success(HomeFixtures.studentSnapshot())
    var teacherResult: DomainResult<HomeSnapshot.Teacher> =
        DomainResult.Success(HomeFixtures.teacherSnapshot())

    var studentCallCount: Int = 0
    var teacherCallCount: Int = 0
    var lastStudentUser: User? = null
    var lastTeacherUser: User? = null

    override suspend fun loadStudentSnapshot(user: User): DomainResult<HomeSnapshot.Student> {
        studentCallCount += 1
        lastStudentUser = user
        return studentResult
    }

    override suspend fun loadTeacherSnapshot(user: User): DomainResult<HomeSnapshot.Teacher> {
        teacherCallCount += 1
        lastTeacherUser = user
        return teacherResult
    }
}
