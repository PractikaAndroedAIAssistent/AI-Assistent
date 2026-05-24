package ru.studentai.feature.tasks.presentation.navigation

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

public object TasksRoutes {

    @Serializable
    public data object List : NavigationRoute

    @Serializable
    public data class Edit(public val itemId: String? = null) : NavigationRoute
}
