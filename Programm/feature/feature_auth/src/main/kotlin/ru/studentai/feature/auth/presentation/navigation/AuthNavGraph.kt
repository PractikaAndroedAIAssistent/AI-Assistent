package ru.studentai.feature.auth.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.studentai.feature.auth.presentation.login.LoginScreen
import ru.studentai.feature.auth.presentation.profile.ProfileScreen
import ru.studentai.feature.auth.presentation.register.RegisterScreen

/**
 * Регистрация destination'ов feature_auth в общем графе навигации.
 *
 * Использование в `app/StudentAiNavGraph.kt`:
 * ```
 * NavHost(navController, startDestination = AuthRoutes.Login) {
 *     authGraph(
 *         onLoggedIn = { navController.navigate(HomeRoute) { popUpToRoot() } },
 *         onLoggedOut = { navController.navigate(AuthRoutes.Login) { popUpToRoot() } },
 *     )
 *     // ... другие graphs других фич
 * }
 * ```
 */
public fun NavGraphBuilder.authGraph(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    composable<AuthRoutes.Login> {
        LoginScreen(
            onLoggedIn = onLoggedIn,
            onNavigateToRegister = onNavigateToRegister,
        )
    }
    composable<AuthRoutes.Register> {
        RegisterScreen(
            onRegistered = onLoggedIn,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
    composable<AuthRoutes.Profile> {
        ProfileScreen(onLoggedOut = onLoggedOut)
    }
}
