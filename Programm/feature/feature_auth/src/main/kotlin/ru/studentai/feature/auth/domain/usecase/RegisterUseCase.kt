package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.repository.AuthRepository

public class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public suspend operator fun invoke(data: RegistrationData): DomainResult<User> =
        repository.register(data)
}
