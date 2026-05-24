package ru.studentai.feature.schedule.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import ru.studentai.feature.schedule.R
import ru.studentai.feature.schedule.domain.model.LessonType

/**
 * Получение локализованной подписи типа занятия.
 *
 * Используется в карточках и форме редактирования. Для [LessonType.Other]
 * UI самостоятельно подставляет `customTypeLabel`, поэтому здесь возвращается
 * generic «Другое».
 */
@Composable
@ReadOnlyComposable
public fun LessonType.localizedLabel(): String = stringResource(
    when (this) {
        LessonType.Lecture -> R.string.feature_schedule_type_lecture
        LessonType.Seminar -> R.string.feature_schedule_type_seminar
        LessonType.Lab -> R.string.feature_schedule_type_lab
        LessonType.Practice -> R.string.feature_schedule_type_practice
        LessonType.Consultation -> R.string.feature_schedule_type_consultation
        LessonType.Exam -> R.string.feature_schedule_type_exam
        LessonType.Other -> R.string.feature_schedule_type_other
    },
)
