package com.example.assistentai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.studentai.core.navigation.compose.NavigatorEffectHandler
import ru.studentai.core.navigation.navigator.Navigator
import ru.studentai.feature.auth.presentation.navigation.AuthRoutes
import ru.studentai.feature.auth.presentation.navigation.authGraph
import ru.studentai.feature.home.presentation.navigation.HomeRoutes
import ru.studentai.feature.home.presentation.navigation.homeGraph

/**
 * Корневой навигационный граф приложения.
 *
 * Текущий состав destination'ов:
 *  • [AuthRoutes.Login] — вход (feature_auth, стартовая точка)
 *  • [AuthRoutes.Register] — регистрация (feature_auth)
 *  • [AuthRoutes.Profile] — экран профиля (feature_auth)
 *  • [HomeRoutes.Home] — главный экран (feature_home), доступен после логина
 *
 * Quick-actions из feature_home пока не имеют целевых экранов (их фичи ещё не реализованы),
 * поэтому колбэк onQuickAction — заглушка-no-op. По мере подключения фич сюда добавятся
 * навигации на конкретные destination'ы.
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
            onQuickAction = {
                // Целевые экраны фич ещё не реализованы — действие игнорируется
                // до подключения соответствующих feature_* модулей (notes/pdf/ai/tests/flashcards/etc.).
            },
        )
    }
}
