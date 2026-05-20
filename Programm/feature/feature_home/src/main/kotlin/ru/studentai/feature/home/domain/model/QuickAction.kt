package ru.studentai.feature.home.domain.model

/**
 * Быстрое действие на главном экране (ТЗ §4.2.2).
 *
 * Для каждой роли — свой набор:
 *  • Студент: NewNote, UploadPdf, OpenAi, NewTest, Flashcards
 *  • Преподаватель: UploadMaterial, CreateTest, OpenAi, Analytics
 *
 * Sealed для exhaustive `when` в presentation-слое.
 */
public sealed interface QuickAction {

    public sealed interface Student : QuickAction {
        public data object NewNote : Student
        public data object UploadPdf : Student
        public data object OpenAi : Student
        public data object NewTest : Student
        public data object Flashcards : Student
    }

    public sealed interface Teacher : QuickAction {
        public data object UploadMaterial : Teacher
        public data object CreateTest : Teacher
        public data object OpenAi : Teacher
        public data object Analytics : Teacher
    }
}
