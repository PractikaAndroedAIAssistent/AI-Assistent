package ru.studentai.core.navigation.navigator

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.studentai.core.navigation.command.NavigationCommand
import ru.studentai.core.navigation.command.NavigationOptions
import ru.studentai.core.navigation.route.NavigationRoute

/**
 * Дефолтная реализация [Navigator].
 *
 * Используется `MutableSharedFlow` (а не Channel) с буфером:
 *  • если в момент эмита `NavigatorEffectHandler` не активен (например, во время
 *    rotation), команды не теряются;
 *  • при переполнении буфера старые команды отбрасываются (DROP_OLDEST) —
 *    разумный default для UI-навигации.
 */
@Singleton
public class DefaultNavigator @Inject constructor() : Navigator {

    private val _commands: MutableSharedFlow<NavigationCommand> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val commands: SharedFlow<NavigationCommand> = _commands.asSharedFlow()

    override suspend fun navigateTo(route: NavigationRoute, options: NavigationOptions) {
        _commands.emit(NavigationCommand.NavigateTo(route, options))
    }

    override suspend fun back() {
        _commands.emit(NavigationCommand.NavigateBack)
    }

    override suspend fun up() {
        _commands.emit(NavigationCommand.NavigateUp)
    }

    override suspend fun popUpTo(route: NavigationRoute, inclusive: Boolean) {
        _commands.emit(NavigationCommand.PopUpTo(route, inclusive))
    }

    private companion object {
        const val BUFFER_CAPACITY = 8
    }
}
