package ru.studentai.feature.auth.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.UserRole

class RegistrationMapperTest {

    private val sut = RegistrationMapper()

    @Test
    fun `toLoginDto copies email and password`() {
        val dto = sut.toLoginDto(AuthCredentials("u@v.ru", "Abc12345"))
        assertThat(dto.email).isEqualTo("u@v.ru")
        assertThat(dto.password).isEqualTo("Abc12345")
    }

    @Test
    fun `toRegisterDto serializes role using serverValue`() {
        val data = RegistrationData(
            fullName = "Иван", email = "i@v.ru", password = "Abc12345",
            role = UserRole.Teacher, university = "MSU",
        )
        val dto = sut.toRegisterDto(data)
        assertThat(dto.role).isEqualTo("teacher")
        assertThat(dto.fullName).isEqualTo("Иван")
        assertThat(dto.university).isEqualTo("MSU")
    }
}
