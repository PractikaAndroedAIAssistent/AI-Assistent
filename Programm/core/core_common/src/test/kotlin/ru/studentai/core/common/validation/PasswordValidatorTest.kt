package ru.studentai.core.common.validation

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test

class PasswordValidatorTest {

    private val sut = PasswordValidator()

    @Test
    fun `valid password passes default policy`() {
        val r = sut.validate("Abcdef12")
        assertThat(r).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `too short password reports too_short code`() {
        val r = sut.validate("Ab1") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_TOO_SHORT)
    }

    @Test
    fun `missing digit reports no_digit code`() {
        val r = sut.validate("Abcdefgh") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_NO_DIGIT)
    }

    @Test
    fun `missing lowercase reports no_lower code`() {
        val r = sut.validate("ABCDEFG1") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_NO_LOWER)
    }

    @Test
    fun `missing uppercase reports no_upper code`() {
        val r = sut.validate("abcdefg1") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_NO_UPPER)
    }

    @Test
    fun `whitespace forbidden`() {
        val r = sut.validate("Abcdef 12") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_WHITESPACE)
    }

    @Test
    fun `empty password reports required`() {
        val r = sut.validate("") as ValidationResult.Invalid
        assertThat(r.errors.single().code).isEqualTo(RequiredFieldValidator.CODE)
    }

    @Test
    fun `requireSpecial enforces special char when enabled`() {
        val strict = PasswordValidator(requireSpecial = true)
        val r = strict.validate("Abcdef12") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_NO_SPECIAL)
    }

    @Test
    fun `requireSpecial passes when special char present`() {
        val strict = PasswordValidator(requireSpecial = true)
        val r = strict.validate("Abcdef12!")
        assertThat(r).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `too long password reports too_long code`() {
        val strict = PasswordValidator(maxLength = 10)
        val r = strict.validate("Abcdefgh12345") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(PasswordValidator.CODE_TOO_LONG)
    }
}
