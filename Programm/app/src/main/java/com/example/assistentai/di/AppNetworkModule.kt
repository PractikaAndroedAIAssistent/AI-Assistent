package com.example.assistentai.di

import com.example.assistentai.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.core.network.client.BaseUrlProvider
import ru.studentai.core.network.client.StaticBaseUrlProvider
import ru.studentai.core.network.interceptor.NetworkLoggingFlag

/**
 * Параметры сетевого слоя, специфичные для приложения.
 *
 *  • [BaseUrlProvider] — точка подключения backend. Placeholder URL до момента,
 *    когда реальный сервер появится; на debug-сборке заменяется через DI на demo-режим
 *    (см. [AppAuthModule] — там вместо `AuthRepositoryImpl` биндится `DemoAuthRepository`,
 *    который не использует BaseUrl).
 *  • [NetworkLoggingFlag] — детальное логирование HTTP включено только в debug-сборке.
 */
@Module
@InstallIn(SingletonComponent::class)
public object AppNetworkModule {

    @Provides
    @Singleton
    public fun provideBaseUrlProvider(): BaseUrlProvider =
        StaticBaseUrlProvider(url = "https://api.studentai.example.com/")

    @Provides
    @Singleton
    public fun provideNetworkLoggingFlag(): NetworkLoggingFlag =
        NetworkLoggingFlag { BuildConfig.DEBUG }
}
