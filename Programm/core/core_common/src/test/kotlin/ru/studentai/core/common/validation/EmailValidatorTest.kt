package ru.studentai.core.common.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailValidatorTest {

    private val sut = EmailValidator()

    @ParameterizedTest
    @ValueSource(strings = [
        "user@example.com",
        "user.name+tag@sub.example.co",
        "u@a.io",
        "test123@university.edu.ru",
    ])
    fun `valid emails pass`(value: String) {
        assertThat(sut.validate(value)).isInstanceOf(ValidationResult.Valid::class)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "",
        "   ",
        "no-at-sign.com",
        "@no-local.com",
        "user@",
        "user@.com",
        "user@domain",
        "user @ space.com",
    ])
    fun `invalid emails fail`(value: String) {
        val r = sut.validate(value)
        assertThat(r).isInstanceOf(ValidationResult.Invalid::class)
    }

    @Test
    fun `empty value reports required code`() {
        val r = sut.validate("") as ValidationResult.Invalid
        assertThat(r.errors.single().code).isEqualTo(RequiredFieldValidator.CODE)
    }

    @Test
    fun `malformed value reports invalid_format code`() {
        val r = sut.validate("not-an-email") as ValidationResult.Invalid
        assertThat(r.errors.single().code).isEqualTo(EmailValidator.CODE_INVALID_FORMAT)
    }

    @Test
    fun `custom field name propagates to ValidationError`() {
        val sut = EmailValidator(field = "userEmail")
        val r = sut.validate("") as ValidationResult.Invalid
        assertThat(r.errors.single().field).isEqualTo("userEmail")
    }
}
