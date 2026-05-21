package ru.studentai.feature.home.domain.model

/**
 * Рекомендация по подготовке (ТЗ §4.2.10 + §4.2.7 — AI-рекомендации).
 *
 * @param id      стабильный ключ
 * @param title   короткий заголовок
 * @param body    развёрнутое описание (1–3 предложения)
 * @param subject связанный предмет (опционально — для бейджа)
 */
public data class Recommendation(
    public val id: String,
    public val title: String,
    public val body: String,
    public val subject: String? = null,
)
