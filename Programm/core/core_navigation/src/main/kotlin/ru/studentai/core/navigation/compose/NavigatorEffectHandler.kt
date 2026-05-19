package ru.studentai.core.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import ru.studentai.core.navigation.command.NavigationCommand
import ru.studentai.core.navigation.command.NavigationOptions
import ru.studentai.core.navigation.navigator.Navigator

/**
 * Lifecycle-aware подписка на [Navigator.commands] и применение их к [NavController].
 *
 * Размещается в root Composable приложения сразу после `rememberNavController()`:
 * ```
 * val navController = rememberNavController()
 * val navigator = hiltEntryPoint().navigator()
 * NavigatorEffectHandler(navigator, navController)
 * NavHost(navController, startDestination = ...) { ... }
 * ```
 */
@Composable
public fun NavigatorEffectHandler(
    navigator: Navigator,
    navController: NavController,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(navigator, navController, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.commands.collect { command ->
                command.apply(navController)
            }
        }
    }
}

private fun NavigationCommand.apply(navController: NavController) {
    when (this) {
        is NavigationCommand.NavigateTo -> navController.navigate(route) {
            applyOptions(options)
        }
        NavigationCommand.NavigateBack -> {
            navController.popBackStack()
        }
        NavigationCommand.NavigateUp -> {
            navController.navigateUp()
        }
        is NavigationCommand.PopUpTo -> {
            navController.popBackStack(route, inclusive)
        }
    }
}

private fun NavOptionsBuilder.applyOptions(options: NavigationOptions) {
    options.popUpTo?.let { dest ->
        popUpTo(dest) {
            inclusive = options.popUpToInclusive
            saveState = options.restoreState
        }
    } ?: run {
        if (options.popUpToInclusive) {
            // popUpToRoot-сценарий: чистим всё до root и эту вершину тоже.
            popUpTo(0) {
                inclusive = true
            } as PopUpToBuilder
        }
    }
    launchSingleTop = options.singleTop
    restoreState = options.restoreState
}
