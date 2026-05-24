package ru.studentai.feature.flashcards.domain.model

/**
 * Уровень знания карточки при повторении (ТЗ §4.2.9): пять кнопок «не знаю / плохо /
 * нормально / хорошо / отлично».
 *
 * `score` (0..4) подаётся в [ru.studentai.feature.flashcards.domain.algorithm.Sm2Algorithm].
 * Это локальный «q» (quality) в терминах SM-2 — в оригинале SM-2 он 0..5, мы используем
 * 0..4 (без отдельного 5 для perfect retrieval), что покрывает реальные UI-сценарии.
 */
public enum class ReviewQuality(public val score: Int) {
    Unknown(0),
    Bad(1),
    Normal(2),
    Good(3),
    Excellent(4),
    ;

    /** Считаем ответ «помнит» если оценка ≥ Normal (как в стандарте SM-2: q ≥ 3 в шкале 0..5). */
    public val isRemembered: Boolean
        get() = score >= Normal.score
}
