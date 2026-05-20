package ru.studentai.feature.auth.domain.model

/**
 * Роль пользователя.
 *
 * ТЗ §2.1: «Система поддерживает две основные роли пользователей: Студент и Преподаватель».
 * Sealed-иерархия — exhaustive `when` в UI/UseCase.
 *
 * Для UI-привязки используется [serverValue] — то, что отправляется на сервер и хранится в БД.
 */
public sealed class UserRole(public val serverValue: String) {

    public data object Student : UserRole(serverValue = "student")

    public data object Teacher : UserRole(serverValue = "teacher")

    public companion object {
        /** Парсинг ответа сервера. Возвращает `null` для неизвестных значений. */
        public fun fromServerValue(value: String): UserRole? = when (value.lowercase()) {
            Student.serverValue -> Student
            Teacher.serverValue -> Teacher
            else -> null
        }
    }
}
