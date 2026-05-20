package ru.studentai.feature.auth.presentation.login

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.validation.EmailValidator
import ru.studentai.core.common.validation.RequiredFieldValidator
import ru.studentai.core.common.validation.ValidationResult
import ru.studentai.core.common.validation.isInvalid
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.usecase.LoginUseCase
import ru.studentai.feature.auth.domain.usecase.ValidateLoginInputUseCase

@HiltViewModel
public class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateInput: ValidateLoginInputUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<LoginState, LoginEvent, LoginEffect>(
    initialState = LoginState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> updateState {
                it.copy(email = event.value, emailError = null, errorMessage = null)
            }
            is LoginEvent.PasswordChanged -> updateState {
                it.copy(password = event.value, passwordError = null, errorMessage = null)
            }
            LoginEvent.SubmitClicked -> submit()
            LoginEvent.NavigateToRegisterClicked -> sendEffect(LoginEffect.NavigateToRegister)
            LoginEvent.ForgotPasswordClicked -> {
                // Зарезервировано — password-reset endpoint появится отдельной задачей.
            }
            LoginEvent.ErrorBannerDismissed -> updateState { it.copy(errorMessage = null) }
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        handleFailure(error)
    }

    private fun submit() {
        val email = currentState.email.trim()
        val password = currentState.password
        val validation = validateInput(email, password)
        if (validation.isInvalid) {
            applyValidation(validation)
            return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null) }
        launchSafe {
            val credentials = AuthCredentials(email = email, password = password)
            when (val result = loginUseCase(credentials)) {
                is DomainResult.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    sendEffect(LoginEffect.NavigateHome)
                }
                is DomainResult.Failure -> handleFailure(result.error)
            }
        }
    }

    private fun applyValidation(validation: ValidationResult) {
        val errors = (validation as ValidationResult.Invalid).errors
        val emailError = errors.firstOrNull { it.field == EmailValidator.DEFAULT_FIELD }?.message
        val passwordError = errors.firstOrNull {
            it.field == "password" && it.code == RequiredFieldValidator.CODE
        }?.message
        updateState { it.copy(emailError = emailError, passwordError = passwordError) }
    }

    private fun handleFailure(error: AppException) {
        val message = errorResolver.resolve(error)
        updateState { it.copy(isSubmitting = false, errorMessage = message) }
        sendEffect(LoginEffect.ShowError(message))
    }
}
