package ru.studentai.core.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Подписка на [Lifecycle.Event] из Composable.
 *
 * Использование (например, в феатур-экране для refresh при возврате на экран):
 * ```
 * OnLifecycleEvent { event ->
 *     if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(MyEvent.Refresh)
 * }
 * ```
 */
@Composable
public fun OnLifecycleEvent(
    onEvent: (event: Lifecycle.Event) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEvent by rememberUpdatedState(onEvent)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> currentOnEvent(event) }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
