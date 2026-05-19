package ru.studentai.core.designsystem.component.feedback

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

/**
 * Тип снэкбара, влияет на цвет фона и иконку (если будет).
 *
 * Маппинг на семантические цвета:
 *  • Info     → colorScheme.inverseSurface (default M3)
 *  • Success  → semanticColors.success
 *  • Warning  → semanticColors.warning
 *  • Error    → colorScheme.errorContainer
 */
@Immutable
public enum class AppSnackbarType { Info, Success, Warning, Error }

/**
 * Хост снэкбаров дизайн-системы.
 * Использовать в [androidx.compose.material3.Scaffold] через `snackbarHost = { AppSnackbarHost(state) }`.
 *
 * Тип берётся из visuals — в простейшем случае оставлен дефолтным (Info).
 * Расширенное API (с типом) появится после внедрения [androidx.compose.material3.SnackbarVisuals].
 */
@Composable
public fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { data ->
            Snackbar(
                snackbarData = data,
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                actionColor = MaterialTheme.colorScheme.inversePrimary,
            )
        },
    )
}
