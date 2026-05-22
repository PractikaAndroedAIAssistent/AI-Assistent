package ru.studentai.feature.schedule.presentation.navigation

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Маршруты модуля расписания.
 *
 *  • [Schedule] — экран списка (день/неделя + фильтр)
 *  • [LessonEdit] — экран создания/редактирования занятия
 */
public object ScheduleRoutes {

    @Serializable
    public data object Schedule : NavigationRoute

    /**
     * @param itemId `null` для создания нового занятия, иначе — id существующего.
     */
    @Serializable
    public data class LessonEdit(public val itemId: String? = null) : NavigationRoute
}
