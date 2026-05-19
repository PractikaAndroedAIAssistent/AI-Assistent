package ru.studentai.core.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Lifecycle-aware подписка на `Flow<T>` для UiEffect'ов.
 *
 * Использование на экране:
 * ```
 * ObserveAsEffects(viewModel.effects) { effect ->
 *     when (effect) {
 *         AuthEffect.NavigateHome -> navigateToHome()
 *         is AuthEffect.ShowError -> snackbarHost.showSnackbar(effect.message)
 *     }
 * }
 * ```
 *
 * Гарантии:
 *  • эффекты обрабатываются только когда экран в STARTED;
 *  • при rotation подписка перезапускается;
 *  • эффекты, эмитнутые во время паузы, дойдут после возврата в STARTED
 *    (благодаря Channel.BUFFERED в [ru.studentai.core.ui.mvi.BaseViewModel]);
 *  • [key] позволяет рестартовать подписку при смене зависимостей.
 */
@Composable
public fun <T> ObserveAsEffects(
    flow: Flow<T>,
    key: Any? = Unit,
    onEffect: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEffect by rememberUpdatedState(onEffect)

    LaunchedEffect(flow, lifecycleOwner, key) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { effect -> currentOnEffect(effect) }
        }
    }
}
