package ru.studentai.feature.auth.presentation.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.auth.domain.usecase.LogoutUseCase
import ru.studentai.feature.auth.domain.usecase.UpdateProfileUseCase

@HiltViewModel
public class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<ProfileState, ProfileEvent, ProfileEffect>(
    initialState = ProfileState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    init {
        dispatch(ProfileEvent.LoadProfile)
    }

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile, ProfileEvent.RetryClicked -> loadProfile()
            ProfileEvent.EditClicked -> startEditing()
            ProfileEvent.CancelEditClicked -> updateState { it.copy(isEditing = false) }
            ProfileEvent.SaveClicked -> saveDraft()
            ProfileEvent.LogoutClicked -> logout()
            is ProfileEvent.FullNameChanged -> updateDraft { it.copy(fullName = event.value) }
            is ProfileEvent.UniversityChanged -> updateDraft { it.copy(university = event.value) }
            is ProfileEvent.GroupChanged -> updateDraft { it.copy(group = event.value) }
            is ProfileEvent.CourseChanged -> updateDraft { it.copy(course = event.value) }
            is ProfileEvent.SpecialityChanged -> updateDraft { it.copy(speciality = event.value) }
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        notifyError(error)
    }

    private fun loadProfile() {
        updateState { it.copy(content = ContentState.Loading) }
        launchSafe {
            when (val result = getProfileUseCase()) {
                is DomainResult.Success -> updateState {
                    it.copy(content = ContentState.Success(result.value))
                }
                is DomainResult.Failure -> updateState {
                    it.copy(content = ContentState.Error(result.error))
                }
            }
        }
    }

    private fun startEditing() {
        val current = (currentState.content as? ContentState.Success<UserProfile>)?.data ?: return
        updateState {
            it.copy(
                isEditing = true,
                draft = ProfileDraft(
                    fullName = current.user.fullName,
                    university = current.university.orEmpty(),
                    group = current.group.orEmpty(),
                    course = current.course?.toString().orEmpty(),
                    speciality = current.speciality.orEmpty(),
                ),
            )
        }
    }

    private fun saveDraft() {
        val current = (currentState.content as? ContentState.Success<UserProfile>)?.data ?: return
        val draft = currentState.draft
        val course = draft.course.trim().toIntOrNull()
        if (draft.course.isNotBlank() && (course == null || course !in 1..6)) {
            sendEffect(ProfileEffect.ShowError("Курс должен быть числом от 1 до 6"))
            return
        }
        val updated = current.copy(
            user = current.user.copy(fullName = draft.fullName.trim()),
            university = draft.university.trim().takeIf(String::isNotEmpty),
            group = draft.group.trim().takeIf(String::isNotEmpty),
            course = course,
            speciality = draft.speciality.trim().takeIf(String::isNotEmpty),
        )
        updateState { it.copy(isSaving = true) }
        launchSafe {
            when (val result = updateProfileUseCase(updated)) {
                is DomainResult.Success -> {
                    updateState {
                        it.copy(
                            content = ContentState.Success(result.value),
                            isSaving = false,
                            isEditing = false,
                        )
                    }
                    sendEffect(ProfileEffect.ShowSavedToast)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isSaving = false) }
                    notifyError(result.error)
                }
            }
        }
    }

    private fun logout() {
        updateState { it.copy(isLoggingOut = true) }
        launchSafe {
            // Игнорируем результат: логаут всегда успешен с точки зрения локального состояния
            // (репозиторий очищает токены даже при сетевой ошибке).
            logoutUseCase()
            updateState { it.copy(isLoggingOut = false) }
            sendEffect(ProfileEffect.LoggedOut)
        }
    }

    private fun updateDraft(transform: (ProfileDraft) -> ProfileDraft) {
        updateState { it.copy(draft = transform(it.draft)) }
    }

    private fun notifyError(error: AppException) {
        sendEffect(ProfileEffect.ShowError(errorResolver.resolve(error)))
    }
}
