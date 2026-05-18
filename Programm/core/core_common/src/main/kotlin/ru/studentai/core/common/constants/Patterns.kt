package ru.studentai.core.common.constants

/**
 * Регулярные выражения и константы, разделяемые между модулями.
 *
 * Регексы скомпилированы один раз (lazy через `val` на object) — не создавать
 * новые `Regex(...)` в hot-path коде.
 */
public object Patterns {

    /**
     * RFC-5322-совместимый практичный regex для email.
     * Не покрывает абсолютно все edge-cases RFC, но соответствует поведению `mailto:`-валидаторов
     * в большинстве почтовых клиентов.
     */
    public val EMAIL: Regex = Regex(
        pattern = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$",
    )

    /**
     * Российский номер мобильного телефона в формате +7XXXXXXXXXX или 8XXXXXXXXXX
     * (разделители-пробелы/дефисы убираются на нормализации до проверки).
     */
    public val PHONE_RU: Regex = Regex(
        pattern = "^(\\+7|8)\\d{10}$",
    )

    /**
     * Имя пользователя для отображения. Кириллица, латиница, пробел, дефис, апостроф.
     * Запрещены цифры, спецсимволы и emoji.
     */
    public val DISPLAY_NAME: Regex = Regex(
        pattern = "^[\\p{L} '\\-]{1,100}$",
    )

    /**
     * UUID v4.
     */
    public val UUID_V4: Regex = Regex(
        pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
    )

    /** Базовый URL (http/https). */
    public val URL: Regex = Regex(
        pattern = "^https?://[\\w.\\-]+(?::\\d+)?(?:/[\\w./\\-?=&%#+]*)?$",
    )
}
