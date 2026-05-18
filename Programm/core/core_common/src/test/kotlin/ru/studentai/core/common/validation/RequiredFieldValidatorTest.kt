package ru.studentai.core.common.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

class RequiredFieldValidatorTest {

    private val sut = RequiredFieldValidator(field = "title")

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = ["   ", "\t", "\n"])
    fun `null blank or whitespace fails`(value: String?) {
        val r = sut.validate(value)
        assertThat(r).isInstanceOf(ValidationResult.Invalid::class)
        val err = (r as ValidationResult.Invalid).errors.single()
        assertThat(err.field).isEqualTo("title")
        assertThat(err.code).isEqualTo(RequiredFieldValidator.CODE)
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "Hello", "   value  "])
    fun `non-blank values pass`(value: String) {
        assertThat(sut.validate(value)).isInstanceOf(ValidationResult.Valid::class)
    }
}
