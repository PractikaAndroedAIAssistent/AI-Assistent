package ru.studentai.feature.schedule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.studentai.feature.schedule.presentation.edit.LessonEditScreen
import ru.studentai.feature.schedule.presentation.schedule.ScheduleScreen

/**
 * NavGraphBuilder-extension для подключения экранов фичи в корневой граф.
 */
public fun NavGraphBuilder.scheduleGraph(
    onNavigateToAddLesson: () -> Unit,
    onNavigateToEditLesson: (String) -> Unit,
    onCloseEditor: () -> Unit,
) {
    composable<ScheduleRoutes.Schedule> {
        ScheduleScreen(
            onNavigateToAddLesson = onNavigateToAddLesson,
            onNavigateToEditLesson = onNavigateToEditLesson,
        )
    }
    composable<ScheduleRoutes.LessonEdit> { entry ->
        val args = entry.toRoute<ScheduleRoutes.LessonEdit>()
        LessonEditScreen(
            itemId = args.itemId,
            onClose = onCloseEditor,
        )
    }
}
