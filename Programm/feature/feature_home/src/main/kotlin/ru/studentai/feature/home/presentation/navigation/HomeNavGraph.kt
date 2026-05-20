package ru.studentai.feature.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.studentai.feature.home.domain.model.QuickAction
import ru.studentai.feature.home.presentation.home.HomeScreen

/**
 * NavGraphBuilder-extension для подключения главного экрана в корневой граф.
 *
 * @param onNavigateToProfile  переход на профиль (обычно AuthRoutes.Profile)
 * @param onQuickAction        обработчик быстрого действия — навигация в feature_*
 *                             (на текущем этапе app может игнорировать незаконченные действия)
 */
public fun NavGraphBuilder.homeGraph(
    onNavigateToProfile: () -> Unit,
    onQuickAction: (QuickAction) -> Unit,
) {
    composable<HomeRoutes.Home> {
        HomeScreen(
            onNavigateToProfile = onNavigateToProfile,
            onQuickAction = onQuickAction,
        )
    }
}
