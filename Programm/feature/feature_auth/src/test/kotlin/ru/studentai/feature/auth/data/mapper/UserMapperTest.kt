package ru.studentai.feature.auth.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.studentai.feature.auth.data.remote.dto.UserDto
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole

class UserMapperTest {

    private val sut = UserMapper()

    @Test
    fun `toDomain maps fields and resolves role`() {
        val dto = UserDto(
            id = "u1",
            email = "ivan@vuz.ru",
            fullName = "Иван",
            role = "student",
        )
        val user = sut.toDomain(dto)
        assertThat(user.id).isEqualTo("u1")
        assertThat(user.email).isEqualTo("ivan@vuz.ru")
        assertThat(user.fullName).isEqualTo("Иван")
        assertThat(user.role).isEqualTo(UserRole.Student as UserRole)
    }

    @Test
    fun `toDomain throws for unknown role`() {
        val dto = UserDto(
            id = "u1", email = "x", fullName = "y", role = "admin",
        )
        assertThrows<IllegalStateException> { sut.toDomain(dto) }
    }

    @Test
    fun `toProfile includes optional fields`() {
        val dto = UserDto(
            id = "u1", email = "x", fullName = "y", role = "teacher",
            university = "MSU", group = null, course = null, speciality = "CS",
        )
        val profile = sut.toProfile(dto)
        assertThat(profile.university).isEqualTo("MSU")
        assertThat(profile.speciality).isEqualTo("CS")
    }

    @Test
    fun `toUpdateRequest copies relevant fields from profile`() {
        val profile = UserProfile(
            user = ru.studentai.feature.auth.domain.model.User(
                id = "u1", email = "x", fullName = "y", role = UserRole.Student,
            ),
            university = "MSU",
            course = 3,
        )
        val req = sut.toUpdateRequest(profile)
        assertThat(req.fullName).isEqualTo("y")
        assertThat(req.university).isEqualTo("MSU")
        assertThat(req.course).isEqualTo(3)
    }
}
