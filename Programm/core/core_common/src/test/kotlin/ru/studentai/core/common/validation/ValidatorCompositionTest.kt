package ru.studentai.core.common.validation

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test

class ValidatorCompositionTest {

    @Test
    fun `plus aggregates errors without short-circuit`() {
        val rule: Validator<String> = LengthValidator("pw", min = 8) + Validator { v ->
            if (v.contains("X")) ValidationResult.Valid
            else ValidationResult.invalid("pw", "no_x", "must contain X")
        }
        val r = rule.validate("ab") as ValidationResult.Invalid
        assertThat(r.errors).hasSize(2)
        assertThat(r.errors.map { it.code })
            .containsExactlyInAnyOrder(LengthValidator.CODE_TOO_SHORT, "no_x")
    }

    @Test
    fun `then short-circuits after first failure`() {
        var secondCalled = false
        val rule: Validator<String> = RequiredFieldValidator("name") then Validator { _ ->
            secondCalled = true
            ValidationResult.Valid
        }
        rule.validate("")
        assertThat(secondCalled).isFalse()
    }

    @Test
    fun `then runs next rule when first passes`() {
        var secondCalled = false
        val rule: Validator<String> = RequiredFieldValidator("name") then Validator { _ ->
            secondCalled = true
            ValidationResult.Valid
        }
        rule.validate("ok")
        check(secondCalled) { "second rule must run when first passes" }
    }

    @Test
    fun `CompositeValidator vararg produces same result as plus chain`() {
        val composite = CompositeValidator(
            LengthValidator("v", min = 3),
            Validator<String> { value ->
                if (value.startsWith("a")) ValidationResult.Valid
                else ValidationResult.invalid("v", "no_prefix", "must start with 'a'")
            },
        )
        val r = composite.validate("xy") as ValidationResult.Invalid
        assertThat(r.errors).hasSize(2)
    }

    @Test
    fun `merge collapses list of results`() {
        val all = listOf(
            ValidationResult.Valid,
            ValidationResult.invalid("a", "x", "x"),
            ValidationResult.invalid("b", "y", "y"),
        )
        val merged = all.merge() as ValidationResult.Invalid
        assertThat(merged.errors).hasSize(2)
    }
}
