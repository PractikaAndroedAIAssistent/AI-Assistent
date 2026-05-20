package ru.studentai.feature.auth.domain.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class UserRoleTest {

    @Test
    fun `fromServerValue parses known roles regardless of case`() {
        assertThat(UserRole.fromServerValue("student")).isEqualTo(UserRole.Student as UserRole)
        assertThat(UserRole.fromServerValue("STUDENT")).isEqualTo(UserRole.Student as UserRole)
        assertThat(UserRole.fromServerValue("teacher")).isEqualTo(UserRole.Teacher as UserRole)
    }

    @Test
    fun `fromServerValue returns null for unknown values`() {
        assertThat(UserRole.fromServerValue("admin")).isNull()
        assertThat(UserRole.fromServerValue("")).isNull()
    }

    @Test
    fun `serverValue exposes stable wire-format string`() {
        assertThat(UserRole.Student.serverValue).isEqualTo("student")
        assertThat(UserRole.Teacher.serverValue).isEqualTo("teacher")
    }
}
