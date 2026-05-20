package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.repository.AuthRepository

public class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public suspend operator fun invoke(): DomainResult<Unit> = repository.logout()
}
