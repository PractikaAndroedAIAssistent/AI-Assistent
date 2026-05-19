package com.example.assistentai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assistentai.welcome.WelcomeRoute
import com.example.assistentai.welcome.WelcomeScreen
import ru.studentai.core.navigation.compose.NavigatorEffectHandler
import ru.studentai.core.navigation.navigator.Navigator
import ru.studentai.feature.auth.presentation.navigation.AuthRoutes
import ru.studentai.feature.auth.presentation.navigation.authGraph

/**
 * Корневой навигационный граф приложения.
 *
 * Текущий состав destination'ов:
 *  • [AuthRoutes.Login] — экран входа (feature_auth)
 *  • [AuthRoutes.Register] — экран регистрации (feature_auth)
 *  • [AuthRoutes.Profile] — экран профиля (feature_auth)
 *  • [WelcomeRoute] — временный «Вход выполнен» (app-уровень) — заменится на
 *    [ru.studentai.feature.home.HomeRoute] после реализации feature_home.
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
                navController.navigate(WelcomeRoute) {
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
        composable<WelcomeRoute> {
            WelcomeScreen(
                onLoggedOut = {
                    navController.navigate(AuthRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
