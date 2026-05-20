package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.validation.EmailValidator
import ru.studentai.core.common.validation.LengthValidator
import ru.studentai.core.common.validation.PasswordValidator
import ru.studentai.core.common.validation.RequiredFieldValidator
import ru.studentai.core.common.validation.ValidationError
import ru.studentai.core.common.validation.ValidationResult
import ru.studentai.core.common.validation.plus

/**
 * Валидация формы регистрации.
 *
 * Проверяет:
 *  • ФИО — непустое, длина 2..100
 *  • email — валидный формат
 *  • password — сложность по дефолтной политике [PasswordValidator]
 *  • passwordRepeat — совпадает с password
 *
 * Все ошибки агрегируются — пользователь видит все проблемы формы разом.
 */
public class ValidateRegisterInputUseCase @Inject constructor() {

    public operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        passwordRepeat: String,
    ): ValidationResult {
        val nameValidator =
            RequiredFieldValidator(field = "fullName") + LengthValidator("fullName", min = 2, max = 100)
        val emailValidator = EmailValidator(field = "email")
        val passwordValidator = PasswordValidator(field = "password")

        val nameResult = nameValidator.validate(fullName)
        val emailResult = emailValidator.validate(email)
        val passwordResult = passwordValidator.validate(password)
        val matchResult = if (password == passwordRepeat) {
            ValidationResult.Valid
        } else {
            ValidationResult.invalid(
                ValidationError(
                    field = "passwordRepeat",
                    code = CODE_PASSWORD_MISMATCH,
                    message = "Passwords do not match",
                ),
            )
        }
        return nameResult + emailResult + passwordResult + matchResult
    }

    public companion object {
        public const val CODE_PASSWORD_MISMATCH: String = "password_mismatch"
    }
}
