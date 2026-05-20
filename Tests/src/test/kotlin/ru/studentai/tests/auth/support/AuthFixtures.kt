package ru.studentai.tests.auth.support

import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole

internal object AuthFixtures {

    fun user(
        id: String = "user-1",
        email: String = "student@example.com",
        fullName: String = "Ivan Petrov",
        role: UserRole = UserRole.Student,
    ): User = User(
        id = id,
        email = email,
        fullName = fullName,
        role = role,
    )

    fun profile(
        user: User = this.user(),
        university: String? = "MSU",
        group: String? = "IU7-41B",
        course: Int? = 3,
        speciality: String? = "Computer Science",
    ): UserProfile = UserProfile(
        user = user,
        university = university,
        group = group,
        course = course,
        speciality = speciality,
    )
}
