package ru.studentai.feature.auth.presentation.register

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.validation.ValidationResult
import ru.studentai.core.common.validation.isInvalid
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.usecase.RegisterUseCase
import ru.studentai.feature.auth.domain.usecase.ValidateRegisterInputUseCase

@HiltViewModel
public class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validateInput: ValidateRegisterInputUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<RegisterState, RegisterEvent, RegisterEffect>(
    initialState = RegisterState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    override fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged -> updateState {
                it.copy(fullName = event.value, fullNameError = null, errorMessage = null)
            }
            is RegisterEvent.EmailChanged -> updateState {
                it.copy(email = event.value, emailError = null, errorMessage = null)
            }
            is RegisterEvent.PasswordChanged -> updateState {
                it.copy(password = event.value, passwordError = null, errorMessage = null)
            }
            is RegisterEvent.PasswordRepeatChanged -> updateState {
                it.copy(
                    passwordRepeat = event.value,
                    passwordRepeatError = null,
                    errorMessage = null,
                )
            }
            is RegisterEvent.RoleSelected -> updateState { it.copy(role = event.role) }
            is RegisterEvent.UniversityChanged -> updateState { it.copy(university = event.value) }
            is RegisterEvent.GroupChanged -> updateState { it.copy(group = event.value) }
            is RegisterEvent.CourseChanged -> updateState { it.copy(course = event.value) }
            is RegisterEvent.SpecialityChanged -> updateState { it.copy(speciality = event.value) }
            RegisterEvent.SubmitClicked -> submit()
            RegisterEvent.NavigateToLoginClicked -> sendEffect(RegisterEffect.NavigateToLogin)
            RegisterEvent.ErrorBannerDismissed -> updateState { it.copy(errorMessage = null) }
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        handleFailure(error)
    }

    private fun submit() {
        val s = currentState
        val validation = validateInput(
            fullName = s.fullName.trim(),
            email = s.email.trim(),
            password = s.password,
            passwordRepeat = s.passwordRepeat,
        )
        if (validation.isInvalid) {
            applyValidation(validation)
            return
        }
        val course = s.course.trim().toIntOrNull()
        if (s.course.isNotBlank() && (course == null || course !in RegistrationData.COURSE_RANGE)) {
            updateState {
                it.copy(errorMessage = "Курс должен быть числом от ${RegistrationData.COURSE_RANGE.first} до ${RegistrationData.COURSE_RANGE.last}")
            }
            return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null) }
        launchSafe {
            val data = RegistrationData(
                fullName = s.fullName.trim(),
                email = s.email.trim(),
                password = s.password,
                role = s.role,
                university = s.university.trim().takeIf(String::isNotEmpty),
                group = s.group.trim().takeIf(String::isNotEmpty),
                course = course,
                speciality = s.speciality.trim().takeIf(String::isNotEmpty),
            )
            when (val result = registerUseCase(data)) {
                is DomainResult.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    sendEffect(RegisterEffect.ShowRegisteredToast)
                    sendEffect(RegisterEffect.NavigateHome)
                }
                is DomainResult.Failure -> handleFailure(result.error)
            }
        }
    }

    private fun applyValidation(validation: ValidationResult) {
        val errors = (validation as ValidationResult.Invalid).errors
        updateState { state ->
            state.copy(
                fullNameError = errors.firstOrNull { it.field == "fullName" }?.message,
                emailError = errors.firstOrNull { it.field == "email" }?.message,
                passwordError = errors.firstOrNull { it.field == "password" }?.message,
                passwordRepeatError = errors.firstOrNull { it.field == "passwordRepeat" }?.message,
            )
        }
    }

    private fun handleFailure(error: AppException) {
        val message = errorResolver.resolve(error)
        updateState { it.copy(isSubmitting = false, errorMessage = message) }
        sendEffect(RegisterEffect.ShowError(message))
    }
}
