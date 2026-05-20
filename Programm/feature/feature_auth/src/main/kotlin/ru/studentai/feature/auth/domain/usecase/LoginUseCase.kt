package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.repository.AuthRepository

/**
 * UseCase входа.
 *
 * Является тонкой обёрткой над репозиторием — не дублирует валидацию (это работа
 * [ValidateLoginInputUseCase], вызываемая ранее в ViewModel).
 */
public class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public suspend operator fun invoke(credentials: AuthCredentials): DomainResult<User> =
        repository.login(credentials)
}
