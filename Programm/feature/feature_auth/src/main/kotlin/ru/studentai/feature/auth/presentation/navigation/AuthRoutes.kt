package ru.studentai.feature.auth.presentation.navigation

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

/** Маршруты модуля авторизации. Все — type-safe через `kotlinx-serialization`. */
public object AuthRoutes {

    @Serializable
    public data object Login : NavigationRoute

    @Serializable
    public data object Register : NavigationRoute

    @Serializable
    public data object Profile : NavigationRoute
}
