package com.example.assistentai.welcome

import kotlinx.serialization.Serializable
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Временный «welcome» destination для наглядной демонстрации, что пользователь вошёл.
 * Будет заменён на `feature_home.HomeRoute` после реализации этой фичи (ТЗ §4.2.2).
 */
@Serializable
public data object WelcomeRoute : NavigationRoute
