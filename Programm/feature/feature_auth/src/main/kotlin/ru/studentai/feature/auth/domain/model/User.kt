package ru.studentai.feature.auth.domain.model

/**
 * Доменная модель пользователя — то, что приходит после login/register.
 *
 * [User] фокусируется на идентификации (id, email, role); полный профиль
 * (вуз, группа, курс) лежит в [UserProfile].
 */
public data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
)
