package ru.studentai.feature.flashcards.presentation.navigation

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

public object FlashcardsRoutes {

    @Serializable
    public data object Sets : NavigationRoute

    @Serializable
    public data class SetEdit(public val setId: String? = null) : NavigationRoute

    @Serializable
    public data class Study(public val setId: String) : NavigationRoute
}
