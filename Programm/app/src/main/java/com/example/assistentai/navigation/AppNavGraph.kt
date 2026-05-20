package com.example.assistentai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.studentai.core.navigation.compose.NavigatorEffectHandler
import ru.studentai.core.navigation.navigator.Navigator
import ru.studentai.feature.auth.presentation.navigation.AuthRoutes
import ru.studentai.feature.auth.presentation.navigation.authGraph
import ru.studentai.feature.home.domain.model.QuickAction
import ru.studentai.feature.home.presentation.navigation.HomeRoutes
import ru.studentai.feature.home.presentation.navigation.homeGraph
import ru.studentai.feature.schedule.presentation.navigation.ScheduleRoutes
import ru.studentai.feature.schedule.presentation.navigation.scheduleGraph
import ru.studentai.feature.tasks.presentation.navigation.TasksRoutes
import ru.studentai.feature.tasks.presentation.navigation.tasksGraph
import ru.studentai.feature.grades.presentation.navigation.GradesRoutes
import ru.studentai.feature.grades.presentation.navigation.gradesGraph

/**
 * Корневой навигационный граф приложения.
 *
 * Destination'ы:
 *  • feature_auth: Login / Register / Profile
 *  • feature_home: Home
 *  • feature_schedule: Schedule / LessonEdit
 *  • feature_tasks: Tasks list / Task edit
 *
 * QuickAction'ы Student/Teacher временно ведут на задачи —
 * последующие фичи добавят свои destination'ы.
 */
@Composable
public fun AppNavGraph(navigator: Navigator) {
    val navController = rememberNavController()

    NavigatorEffectHandler(navigator = navigator, navController = navController)

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Login,
    ) {
        authGraph(
            onLoggedIn = {
                navController.navigate(HomeRoutes.Home) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onLoggedOut = {
                navController.navigate(AuthRoutes.Login) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToLogin = {
                navController.navigate(AuthRoutes.Login) {
                    popUpTo(AuthRoutes.Login) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToRegister = {
                navController.navigate(AuthRoutes.Register)
            },
        )
        homeGraph(
            onNavigateToProfile = {
                navController.navigate(AuthRoutes.Profile)
            },
            onQuickAction = { action ->
                when (action) {
                    is QuickAction.Student, is QuickAction.Teacher ->
                        navController.navigate(TasksRoutes.List)
                }
            },
        )
        scheduleGraph(
            onNavigateToAddLesson = {
                navController.navigate(ScheduleRoutes.LessonEdit(itemId = null))
            },
            onNavigateToEditLesson = { id ->
                navController.navigate(ScheduleRoutes.LessonEdit(itemId = id))
            },
            onCloseEditor = { navController.popBackStack() },
        )
        tasksGraph(
            onNavigateToAdd = {
                navController.navigate(TasksRoutes.Edit(itemId = null))
            },
            onNavigateToEdit = { id ->
                navController.navigate(TasksRoutes.Edit(itemId = id))
            },
            onCloseEditor = { navController.popBackStack() },
        )
        gradesGraph(
            onNavigateToAdd = {
                navController.navigate(GradesRoutes.Edit(itemId = null))
            },
            onNavigateToEdit = { id ->
                navController.navigate(GradesRoutes.Edit(itemId = id))
            },
            onCloseEditor = { navController.popBackStack() },
        )
    }
}
