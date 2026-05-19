package ru.studentai.core.navigation.navigator

import kotlinx.coroutines.flow.SharedFlow
import ru.studentai.core.navigation.command.NavigationCommand
import ru.studentai.core.navigation.command.NavigationOptions
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Глобальный навигатор приложения.
 *
 * Цель: ViewModel'и могут запускать навигацию **не имея доступа к NavController**
 * (а у них его и не должно быть — Context/Activity-зависимость недопустима в VM).
 *
 * Использование в ViewModel:
 * ```
 * fun onLoginClicked() {
 *     launchSafe {
 *         val result = loginUseCase(...)
 *         if (result.isSuccess) navigator.navigateTo(HomeRoute, NavigationOptions.popUpToRoot())
 *     }
 * }
 * ```
 *
 * Подписка в Composable:
 * ```
 * val navController = rememberNavController()
 * NavigatorEffectHandler(navigator, navController)
 * ```
 */
public interface Navigator {

    /** Поток команд навигации; подписчик — `NavigatorEffectHandler`. */
    public val commands: SharedFlow<NavigationCommand>

    public suspend fun navigateTo(
        route: NavigationRoute,
        options: NavigationOptions = NavigationOptions.Default,
    )

    public suspend fun back()

    public suspend fun up()

    public suspend fun popUpTo(route: NavigationRoute, inclusive: Boolean = false)
}
