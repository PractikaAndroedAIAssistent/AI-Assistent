package ru.studentai.feature.home.domain.usecase

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.flatMap
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.repository.HomeRepository

/**
 * UseCase для главного экрана.
 *
 * Алгоритм:
 *  1. Получить профиль через [GetProfileUseCase] (feature_auth).
 *  2. По роли пользователя — вызвать соответствующий метод [HomeRepository].
 *
 * Любая ошибка пробрасывается как `DomainResult.Failure` — UI рендерит ErrorState.
 */
public class GetHomeSnapshotUseCase @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val repository: HomeRepository,
) {

    public suspend operator fun invoke(): DomainResult<HomeSnapshot> {
        return getProfile().flatMap { profile ->
            val user = profile.user
            when (user.role) {
                UserRole.Student -> repository.loadStudentSnapshot(user)
                UserRole.Teacher -> repository.loadTeacherSnapshot(user)
            }
        }
    }
}
