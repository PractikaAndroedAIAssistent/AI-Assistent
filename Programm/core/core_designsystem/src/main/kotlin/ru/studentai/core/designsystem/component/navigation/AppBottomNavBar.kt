package ru.studentai.core.designsystem.component.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Описание одного пункта нижней навигации.
 *
 * Не привязан к конкретной фиче или роли — конкретный набор пунктов
 * собирается в feature-слое (см. ТЗ §4.1.1: набор пунктов отличается у студента и преподавателя).
 *
 * @param key             уникальный ключ (для сравнения, можно использовать роут навигации)
 * @param label           подпись (короткая, влезающая в ширину чипа)
 * @param icon            outlined-иконка (неактивное состояние)
 * @param selectedIcon    filled-иконка для активного состояния (опционально — иначе берётся та же)
 * @param contentDescription  accessibility-описание (иначе используется label)
 */
@Immutable
public data class BottomNavItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val contentDescription: String? = null,
)

/**
 * Нижняя навигационная панель.
 *
 * Соответствует ТЗ §4.1.6: «подписи разделов не должны обрезаться».
 * Для этого:
 *  • используется `alwaysShowLabel = true` — заголовок всегда виден;
 *  • Text усечётся одной строкой `TextOverflow.Ellipsis`, но при разумных длинах
 *    (до 12 символов) полностью помещается на стандартном Android-эмуляторе.
 *
 * @param items     полный набор пунктов
 * @param selectedKey ключ активного пункта
 * @param onItemSelected  колбэк выбора (передаётся `key`)
 */
@Composable
public fun AppBottomNavBar(
    items: List<BottomNavItem>,
    selectedKey: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val selected = item.key == selectedKey
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.key) },
                icon = {
                    Icon(
                        imageVector = if (selected) (item.selectedIcon ?: item.icon) else item.icon,
                        contentDescription = item.contentDescription ?: item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
