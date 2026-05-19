package ru.studentai.core.designsystem.component.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.studentai.core.designsystem.component.feedback.AppSnackbarHost

/**
 * Унифицированный Scaffold для всех экранов фич.
 *
 * Слоты:
 *  • [topBar]         — TopAppBar (рекомендуется [ru.studentai.core.designsystem.component.navigation.AppTopBar])
 *  • [bottomBar]      — обычно [ru.studentai.core.designsystem.component.navigation.AppBottomNavBar]
 *  • [floatingActionButton] — FAB
 *  • [snackbarHostState] — состояние снэкбаров; если не передано, создаётся новое
 *  • [content]        — лямбда контента, получает [PaddingValues] с учётом баров
 *
 * Преимущество перед прямым Material3 Scaffold: автоматический хост снэкбаров
 * дизайн-системы + дефолтный фон через `colorScheme.background`.
 */
@Composable
public fun ScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets: androidx.compose.foundation.layout.WindowInsets =
        androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
    ) { padding ->
        content(padding)
    }
}

/**
 * Удобный вариант: контент уже отрисован с учётом отступов баров — внутрь padding не передаётся.
 * Полезен, когда экрану не нужны топ/боттом-бар.
 */
@Composable
public fun ScreenScaffoldFullBleed(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ScreenScaffold(
        modifier = modifier,
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            content()
        }
    }
}
