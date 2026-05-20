package ru.studentai.feature.home.domain.model

/**
 * Динамика среднего балла (ТЗ §4.2.10 — рассчитывается feature_grades).
 *
 * `Up`/`Down`/`Flat` достаточно для главного экрана; точные значения «дельты»
 * есть на экране оценок.
 */
public enum class ScoreTrend {
    Up,
    Down,
    Flat,
}
