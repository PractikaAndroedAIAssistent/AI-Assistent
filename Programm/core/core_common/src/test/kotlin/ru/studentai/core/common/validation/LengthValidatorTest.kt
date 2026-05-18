package ru.studentai.core.common.validation

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LengthValidatorTest {

    @Test
    fun `value within bounds passes`() {
        val sut = LengthValidator("name", min = 3, max = 10)
        assertThat(sut.validate("Hello")).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `value below min fails with too_short`() {
        val sut = LengthValidator("name", min = 5)
        val r = sut.validate("Hi") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(LengthValidator.CODE_TOO_SHORT)
    }

    @Test
    fun `value above max fails with too_long`() {
        val sut = LengthValidator("name", min = 0, max = 3)
        val r = sut.validate("12345") as ValidationResult.Invalid
        assertThat(r.errors.map { it.code }).contains(LengthValidator.CODE_TOO_LONG)
    }

    @Test
    fun `null max means unbounded`() {
        val sut = LengthValidator("name", min = 0, max = null)
        assertThat(sut.validate("a".repeat(10_000))).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `boundary values are inclusive`() {
        val sut = LengthValidator("name", min = 3, max = 5)
        assertThat(sut.validate("abc")).isInstanceOf(ValidationResult.Valid::class)
        assertThat(sut.validate("abcde")).isInstanceOf(ValidationResult.Valid::class)
    }

    @Test
    fun `negative min in constructor throws`() {
        assertThrows<IllegalArgumentException> { LengthValidator("name", min = -1) }
    }

    @Test
    fun `max less than min throws`() {
        assertThrows<IllegalArgumentException> {
            LengthValidator("name", min = 10, max = 5)
        }
    }
}
