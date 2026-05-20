package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.validation.EmailValidator
import ru.studentai.core.common.validation.RequiredFieldValidator
import ru.studentai.core.common.validation.ValidationResult
import ru.studentai.core.common.validation.plus

/**
 * Валидация формы входа.
 *
 * Не зависит от UI и репозитория — это чистая логика на validator'ах из core_common.
 *
 * @return [ValidationResult] (Valid / Invalid(errors)). Presentation-слой использует это
 *         для подсветки конкретных полей с конкретными сообщениями ошибок.
 */
public class ValidateLoginInputUseCase @Inject constructor() {

    public operator fun invoke(email: String, password: String): ValidationResult {
        val emailRule = EmailValidator()
        val passwordRequired = RequiredFieldValidator(field = "password")
        return emailRule.validate(email) + passwordRequired.validate(password)
    }
}
