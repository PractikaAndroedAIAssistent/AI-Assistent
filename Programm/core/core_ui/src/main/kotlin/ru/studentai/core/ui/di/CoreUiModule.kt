package ru.studentai.core.ui.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.core.common.dispatchers.DefaultDispatcherProvider
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.ui.error.DefaultErrorMessageResolver
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.resource.AndroidResourceProvider
import ru.studentai.core.ui.resource.ResourceProvider

/**
 * Hilt-модуль `core_ui`:
 *  • биндит [ResourceProvider] → [AndroidResourceProvider];
 *  • биндит [ErrorMessageResolver] → [DefaultErrorMessageResolver];
 *  • биндит [DispatcherProvider] (из `core_common`) — через @Provides.
 *
 * Для логирования используется [NoOpLogger] по умолчанию. `app`-модуль может
 * переопределить биндинг через свой Hilt-модуль (например, Timber-обёртку).
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class CoreUiBindings {

    @Binds
    @Singleton
    public abstract fun bindResourceProvider(impl: AndroidResourceProvider): ResourceProvider

    @Binds
    @Singleton
    public abstract fun bindErrorMessageResolver(impl: DefaultErrorMessageResolver): ErrorMessageResolver
}

@Module
@InstallIn(SingletonComponent::class)
public object CoreUiProviders {

    @Provides
    @Singleton
    public fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    public fun provideLogger(): Logger = NoOpLogger
}
