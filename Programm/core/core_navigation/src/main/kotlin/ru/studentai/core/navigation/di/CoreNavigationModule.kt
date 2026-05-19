package ru.studentai.core.navigation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.core.navigation.navigator.DefaultNavigator
import ru.studentai.core.navigation.navigator.Navigator

/**
 * Hilt-биндинги `core_navigation`.
 *
 * [Navigator] — singleton, заинжектится в любую VM через стандартный @Inject.
 * NavController создаётся внутри Compose (`rememberNavController()`) и не идёт через DI.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class CoreNavigationBindings {

    @Binds
    @Singleton
    public abstract fun bindNavigator(impl: DefaultNavigator): Navigator
}
