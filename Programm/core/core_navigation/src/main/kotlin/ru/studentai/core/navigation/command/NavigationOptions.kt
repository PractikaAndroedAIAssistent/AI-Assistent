package ru.studentai.core.navigation.command

import androidx.compose.runtime.Immutable
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Параметры навигации — иммутабельные опции для [NavigationCommand.NavigateTo].
 *
 * @param popUpTo        очистить back stack вплоть до этого route (опционально)
 * @param popUpToInclusive  если `true`, popUpTo-route тоже удаляется
 * @param singleTop      переиспользовать существующий destination, если он уже на вершине
 * @param restoreState   восстановить сохранённое состояние (для bottom-nav переключений)
 * @param launchSingleTop алиас, оставлен для совместимости с привычным API
 */
@Immutable
public data class NavigationOptions(
    public val popUpTo: NavigationRoute? = null,
    public val popUpToInclusive: Boolean = false,
    public val singleTop: Boolean = false,
    public val restoreState: Boolean = false,
) {
    public companion object {
        /** Дефолтные опции — без специальных правил. */
        public val Default: NavigationOptions = NavigationOptions()

        /**
         * Очистить весь back stack и перейти на новый route как root.
         * Используется после успешного логина / после logout.
         */
        public fun popUpToRoot(): NavigationOptions = NavigationOptions(
            popUpTo = null,
            popUpToInclusive = true,
            singleTop = true,
        )

        /**
         * Переключение между вкладками нижней навигации:
         * сохраняем state, не плодим дубли.
         */
        public fun bottomNavSwitch(startDestination: NavigationRoute): NavigationOptions =
            NavigationOptions(
                popUpTo = startDestination,
                popUpToInclusive = false,
                singleTop = true,
                restoreState = true,
            )
    }
}
