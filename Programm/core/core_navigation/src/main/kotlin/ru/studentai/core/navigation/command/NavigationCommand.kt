package ru.studentai.core.navigation.command

import androidx.compose.runtime.Immutable
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Команда навигации, идущая от [Navigator] к [NavigatorEffectHandler].
 *
 * Sealed для exhaustive `when` в обработчике.
 */
@Immutable
public sealed interface NavigationCommand {

    /** Перейти на route с указанными опциями. */
    public data class NavigateTo(
        val route: NavigationRoute,
        val options: NavigationOptions = NavigationOptions.Default,
    ) : NavigationCommand

    /** Назад по back stack'у (== `navController.popBackStack()`). */
    public data object NavigateBack : NavigationCommand

    /** Up-навигация — то же, но с учётом parent-иерархии Compose Navigation. */
    public data object NavigateUp : NavigationCommand

    /** Очистить back stack до указанного route. */
    public data class PopUpTo(
        val route: NavigationRoute,
        val inclusive: Boolean = false,
    ) : NavigationCommand
}
