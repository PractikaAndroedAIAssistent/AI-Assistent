package ru.studentai.feature.auth.domain.model

/**
 * Полный профиль пользователя для экрана «Профиль» (ТЗ §4.2.1).
 *
 * `university`, `group`, `course`, `speciality` опциональны — могут быть пустыми
 * у только что зарегистрированного преподавателя или при импорте из ЛК вуза без этих данных.
 */
public data class UserProfile(
    val user: User,
    val university: String? = null,
    val group: String? = null,
    val course: Int? = null,
    val speciality: String? = null,
) {
    init {
        require(course == null || course in 1..6) { "Course must be in 1..6, got $course" }
    }
}
