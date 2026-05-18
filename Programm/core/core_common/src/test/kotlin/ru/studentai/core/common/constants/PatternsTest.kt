package ru.studentai.core.common.constants

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PatternsTest {

    @ParameterizedTest
    @ValueSource(strings = ["+79991234567", "89991234567"])
    fun `valid russian phone numbers match`(value: String) {
        assertThat(Patterns.PHONE_RU.matches(value)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["+7999", "9991234567", "+7-999-123-45-67", "+89991234567"])
    fun `invalid russian phone numbers do not match`(value: String) {
        assertThat(Patterns.PHONE_RU.matches(value)).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "Иван",
        "Иван Иванов",
        "John O'Neill",
        "Анна-Мария",
    ])
    fun `valid display names match`(value: String) {
        assertThat(Patterns.DISPLAY_NAME.matches(value)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["John123", "user_name", "test@example", ""])
    fun `invalid display names do not match`(value: String) {
        assertThat(Patterns.DISPLAY_NAME.matches(value)).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "550e8400-e29b-41d4-a716-446655440000",
        "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    ])
    fun `valid uuids match`(value: String) {
        assertThat(Patterns.UUID_V4.matches(value)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "https://example.com",
        "http://localhost:8080/api",
        "https://api.studentai.ru/v1/users?id=42",
    ])
    fun `valid urls match`(value: String) {
        assertThat(Patterns.URL.matches(value)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["ftp://example.com", "example.com", "://no-protocol"])
    fun `invalid urls do not match`(value: String) {
        assertThat(Patterns.URL.matches(value)).isFalse()
    }
}
