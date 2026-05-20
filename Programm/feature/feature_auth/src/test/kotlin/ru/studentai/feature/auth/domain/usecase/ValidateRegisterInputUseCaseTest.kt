package ru.studentai.feature.auth.domain.usecase

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test
import ru.studentai.core.common.validation.PasswordValidator
import ru.studentai.core.common.validation.ValidationResult

class ValidateRegisterInputUseCaseTest {

    private val sut = ValidateRegisterInputUseCase()

    @Test
    fun `valid input yields Valid`() {
        val result = sut(
            fullName = "Иван Петров",
            email = "ivan@vuz.ru",
            password = "Abcdef12",
            passwordRepeat = "Abcdef12",
        )
        assertThat(result).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `password mismatch is reported`() {
        val result = sut(
            fullName = "Иван",
            email = "ivan@vuz.ru",
            password = "Abcdef12",
            passwordRepeat = "Abcdef99",
        ) as ValidationResult.Invalid
        assertThat(result.errors.map { it.code })
            .contains(ValidateRegisterInputUseCase.CODE_PASSWORD_MISMATCH)
    }

    @Test
    fun `weak password is reported via PasswordValidator codes`() {
        val result = sut(
            fullName = "Иван",
            email = "ivan@vuz.ru",
            password = "short",
            passwordRepeat = "short",
        ) as ValidationResult.Invalid
        assertThat(result.errors.map { it.code }).contains(PasswordValidator.CODE_TOO_SHORT)
    }

    @Test
    fun `empty fullName is reported`() {
        val result = sut(
            fullName = "",
            email = "ivan@vuz.ru",
            password = "Abcdef12",
            passwordRepeat = "Abcdef12",
        ) as ValidationResult.Invalid
        assertThat(result.errors.map { it.field }).contains("fullName")
    }

    @Test
    fun `invalid email is reported`() {
        val result = sut(
            fullName = "Иван",
            email = "not-an-email",
            password = "Abcdef12",
            passwordRepeat = "Abcdef12",
        ) as ValidationResult.Invalid
        assertThat(result.errors.map { it.field }).contains("email")
    }
}
