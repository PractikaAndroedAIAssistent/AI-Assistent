package ru.studentai.feature.auth.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.repository.AuthRepository

public class UpdateProfileUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    public suspend operator fun invoke(profile: UserProfile): DomainResult<UserProfile> =
        repository.updateProfile(profile)
}
