package ru.studentai.feature.auth.domain.model

/**
 * Данные для регистрации нового пользователя.
 * ТЗ §4.2.1: ФИО, email, роль, вуз, группа, курс, специальность.
 *
 * @param fullName        полное имя пользователя
 * @param email           email
 * @param password        пароль (передаётся на сервер; не сохраняется локально)
 * @param role            выбранная роль
 * @param university      название вуза (опционально на этапе регистрации)
 * @param group           группа (актуально для студентов)
 * @param course          курс (1..6, опционально)
 * @param speciality      специальность (опционально)
 *
 * `toString` скрывает пароль — защита от случайных утечек.
 */
public class RegistrationData(
    public val fullName: String,
    public val email: String,
    public val password: String,
    public val role: UserRole,
    public val university: String? = null,
    public val group: String? = null,
    public val course: Int? = null,
    public val speciality: String? = null,
) {
    init {
        require(course == null || course in COURSE_RANGE) {
            "Course must be in $COURSE_RANGE, got $course"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistrationData) return false
        return fullName == other.fullName &&
            email == other.email &&
            password == other.password &&
            role == other.role &&
            university == other.university &&
            group == other.group &&
            course == other.course &&
            speciality == other.speciality
    }

    override fun hashCode(): Int {
        var result = fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (university?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (course ?: 0)
        result = 31 * result + (speciality?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "RegistrationData(fullName=$fullName, email=$email, password=***, role=$role, " +
            "university=$university, group=$group, course=$course, speciality=$speciality)"

    public companion object {
        public val COURSE_RANGE: IntRange = 1..6
    }
}
