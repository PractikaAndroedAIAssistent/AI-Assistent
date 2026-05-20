package ru.studentai.feature.home.presentation.navigation

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

/** Маршруты модуля главного экрана. */
public object HomeRoutes {

    @Serializable
    public data object Home : NavigationRoute
}
