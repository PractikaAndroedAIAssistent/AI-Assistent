package ru.studentai.feature.auth.domain.usecase

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test
import ru.studentai.core.common.validation.RequiredFieldValidator
import ru.studentai.core.common.validation.ValidationResult

class ValidateLoginInputUseCaseTest {

    private val sut = ValidateLoginInputUseCase()

    @Test
    fun `valid email and non-empty password yield Valid`() {
        assertThat(sut("user@example.com", "anything"))
            .isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `invalid email yields Invalid with email field`() {
        val result = sut("not-an-email", "x") as ValidationResult.Invalid
        assertThat(result.errors.map { it.field }).contains("email")
    }

    @Test
    fun `empty password yields Invalid with password required code`() {
        val result = sut("user@example.com", "") as ValidationResult.Invalid
        val passwordError = result.errors.first { it.field == "password" }
        assertThat(passwordError.code).contains(RequiredFieldValidator.CODE)
    }
}
