package ru.studentai.feature.auth.data.mapper

import javax.inject.Inject
import ru.studentai.feature.auth.data.remote.dto.UpdateProfileRequestDto
import ru.studentai.feature.auth.data.remote.dto.UserDto
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole

/**
 * Маппер DTO ↔ domain для пользователя и его профиля.
 *
 * Бросает [IllegalStateException] при неизвестной роли — это случай, когда сервер
 * вернул нерасшифровываемое значение. Чтобы не валить весь экран, маппер вызывается
 * внутри `safeApiCall`, и ошибка приходит как `NetworkException.Serialization`.
 */
public class UserMapper @Inject constructor() {

    public fun toDomain(dto: UserDto): User =
        User(
            id = dto.id,
            email = dto.email,
            fullName = dto.fullName,
            role = parseRole(dto.role),
        )

    public fun toProfile(dto: UserDto): UserProfile =
        UserProfile(
            user = toDomain(dto),
            university = dto.university,
            group = dto.group,
            course = dto.course,
            speciality = dto.speciality,
        )

    public fun toUpdateRequest(profile: UserProfile): UpdateProfileRequestDto =
        UpdateProfileRequestDto(
            fullName = profile.user.fullName,
            university = profile.university,
            group = profile.group,
            course = profile.course,
            speciality = profile.speciality,
        )

    private fun parseRole(serverValue: String): UserRole =
        UserRole.fromServerValue(serverValue)
            ?: error("Unknown role from server: '$serverValue'")
}
