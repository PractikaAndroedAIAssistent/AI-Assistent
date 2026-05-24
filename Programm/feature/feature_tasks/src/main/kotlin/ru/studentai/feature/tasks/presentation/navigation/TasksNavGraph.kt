package ru.studentai.feature.tasks.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.studentai.feature.tasks.presentation.edit.TaskEditScreen
import ru.studentai.feature.tasks.presentation.list.TasksScreen

public fun NavGraphBuilder.tasksGraph(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onCloseEditor: () -> Unit,
) {
    composable<TasksRoutes.List> {
        TasksScreen(
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToEdit = onNavigateToEdit,
        )
    }
    composable<TasksRoutes.Edit> { entry ->
        val args = entry.toRoute<TasksRoutes.Edit>()
        TaskEditScreen(itemId = args.itemId, onClose = onCloseEditor)
    }
}
