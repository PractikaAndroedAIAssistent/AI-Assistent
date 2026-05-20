package ru.studentai.feature.home.domain.model

/**
 * Сводка среднего балла студента (ТЗ §4.2.10).
 *
 * @param value          текущее значение (например, 4.32)
 * @param maxValue       масштаб шкалы (обычно 5.0 для российских вузов; 10.0 для интервальной)
 * @param trend          динамика по сравнению с предыдущим периодом
 * @param subjectCount   число предметов, по которым есть оценки (для контекста UI)
 */
public data class AverageScoreSummary(
    public val value: Double,
    public val maxValue: Double,
    public val trend: ScoreTrend,
    public val subjectCount: Int,
) {
    init {
        require(value in 0.0..maxValue) { "value $value out of [0, $maxValue]" }
        require(maxValue > 0.0) { "maxValue must be > 0, got $maxValue" }
        require(subjectCount >= 0) { "subjectCount must be >= 0, got $subjectCount" }
    }
}
