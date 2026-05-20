package ru.studentai.feature.auth.domain.model

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegistrationDataTest {

    @Test
    fun `toString does not leak password`() {
        val data = RegistrationData(
            fullName = "Иван Петров",
            email = "ivan@vuz.ru",
            password = "S3cret!Pass",
            role = UserRole.Student,
        )
        val s = data.toString()
        assertThat(s).contains("ivan@vuz.ru")
        assertThat(s).doesNotContain("S3cret!Pass")
    }

    @Test
    fun `course out of range throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            RegistrationData(
                fullName = "x", email = "a@b.com", password = "p",
                role = UserRole.Student, course = 0,
            )
        }
        assertThrows<IllegalArgumentException> {
            RegistrationData(
                fullName = "x", email = "a@b.com", password = "p",
                role = UserRole.Student, course = 7,
            )
        }
    }

    @Test
    fun `null course is accepted`() {
        // Не бросает.
        RegistrationData(
            fullName = "x", email = "a@b.com", password = "p",
            role = UserRole.Student, course = null,
        )
    }
}
