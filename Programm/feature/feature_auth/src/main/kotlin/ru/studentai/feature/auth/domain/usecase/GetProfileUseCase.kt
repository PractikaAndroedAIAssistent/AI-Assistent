package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.repository.AuthRepository

public class GetProfileUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public suspend operator fun invoke(): DomainResult<UserProfile> = repository.currentProfile()
}
