package ru.studentai.core.designsystem.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ru.studentai.core.designsystem.icon.StudentAiIcons

/**
 * Стандартный TopAppBar с поддержкой навигационной иконки и action-кнопок.
 *
 * Дизайн:
 *  • выравнивание заголовка — left (Material 3 default);
 *  • тема следует за scroll-поведением через [scrollBehavior];
 *  • высота — Material standard 64dp.
 *
 * @param title           заголовок экрана
 * @param onNavigateBack  обработчик кнопки «назад»; если null — кнопки нет
 * @param actions         action-иконки в правой части (опционально)
 * @param scrollBehavior  поведение прокрутки (см. [TopAppBarDefaults.pinnedScrollBehavior])
 */
@Composable
public fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    backContentDescription: String = "Назад",
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { Text(title, maxLines = 1) },
        modifier = modifier,
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = StudentAiIcons.Back,
                        contentDescription = backContentDescription,
                    )
                }
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Вариант с центрированным заголовком — для root-экранов нижней навигации.
 */
@Composable
public fun AppCenterAlignedTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    backContentDescription: String = "Назад",
    leadingIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title, maxLines = 1) },
        modifier = modifier,
        navigationIcon = {
            when {
                onNavigateBack != null -> {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = StudentAiIcons.Back,
                            contentDescription = backContentDescription,
                        )
                    }
                }
                leadingIcon != null -> {
                    Icon(imageVector = leadingIcon, contentDescription = null)
                }
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}
