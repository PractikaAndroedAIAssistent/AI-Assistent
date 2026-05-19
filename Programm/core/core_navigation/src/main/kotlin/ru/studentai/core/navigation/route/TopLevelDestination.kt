package ru.studentai.core.navigation.route

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Описание раздела нижней навигации.
 *
 * Не привязан к конкретной роли — наборы [TopLevelDestination] для студента
 * и преподавателя задаются в `app`-модуле или feature_home (ТЗ §4.1.1).
 *
 * @param route             route, на который ведёт нажатие
 * @param label             подпись в нижней навигации
 * @param icon              иконка (outlined, для неактивного состояния)
 * @param selectedIcon      иконка для активного состояния (опционально)
 * @param contentDescription accessibility-описание
 */
@Immutable
public data class TopLevelDestination(
    public val route: NavigationRoute,
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector? = null,
    public val contentDescription: String? = null,
)
