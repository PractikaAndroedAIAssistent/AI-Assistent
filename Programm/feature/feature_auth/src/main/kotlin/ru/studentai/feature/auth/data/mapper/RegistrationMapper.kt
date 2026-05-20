package ru.studentai.feature.auth.data.mapper

import javax.inject.Inject
import ru.studentai.feature.auth.data.remote.dto.LoginRequestDto
import ru.studentai.feature.auth.data.remote.dto.RegisterRequestDto
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData

/**
 * Маппер domain → request DTO для login/register.
 */
public class RegistrationMapper @Inject constructor() {

    public fun toLoginDto(credentials: AuthCredentials): LoginRequestDto =
        LoginRequestDto(email = credentials.email, password = credentials.password)

    public fun toRegisterDto(data: RegistrationData): RegisterRequestDto =
        RegisterRequestDto(
            fullName = data.fullName,
            email = data.email,
            password = data.password,
            role = data.role.serverValue,
            university = data.university,
            group = data.group,
            course = data.course,
            speciality = data.speciality,
        )
}
