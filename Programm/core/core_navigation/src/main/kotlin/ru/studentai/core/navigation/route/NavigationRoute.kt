package ru.studentai.core.navigation.route

/**
 * Маркер для всех navigation routes проекта.
 *
 * Реализации:
 * ```
 * @Serializable
 * data object LoginRoute : NavigationRoute
 *
 * @Serializable
 * data class ProfileRoute(val userId: String) : NavigationRoute
 * ```
 *
 * Compose Navigation 2.8+ поддерживает type-safe routes на основе
 * kotlinx-serialization. Регистрация:
 * ```
 * composable<ProfileRoute> { backStackEntry ->
 *     val route: ProfileRoute = backStackEntry.toRoute()
 *     ProfileScreen(userId = route.userId)
 * }
 * ```
 */
public interface NavigationRoute
