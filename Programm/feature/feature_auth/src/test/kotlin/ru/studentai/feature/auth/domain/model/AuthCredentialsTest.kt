package ru.studentai.feature.auth.domain.model

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import org.junit.jupiter.api.Test

class AuthCredentialsTest {

    @Test
    fun `toString does not leak password`() {
        val creds = AuthCredentials(email = "a@b.com", password = "S3cret!Pass")
        val s = creds.toString()
        assertThat(s).contains("a@b.com")
        assertThat(s).doesNotContain("S3cret!Pass")
        assertThat(s).contains("***")
    }
}
