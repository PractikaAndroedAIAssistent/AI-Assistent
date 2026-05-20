package ru.studentai.feature.auth.domain.model

/**
 * Данные для входа: email и пароль.
 *
 * Не `data class` со стандартным `toString` — пароль не должен попасть в логи.
 * Переопределяем [toString] вручную.
 */
public class AuthCredentials(
    public val email: String,
    public val password: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthCredentials) return false
        return email == other.email && password == other.password
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }

    /** Скрываем пароль в `toString` для защиты от случайных утечек в логи. */
    override fun toString(): String = "AuthCredentials(email=$email, password=***)"
}
