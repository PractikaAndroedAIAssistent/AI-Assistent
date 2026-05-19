package ru.studentai.core.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.studentai.core.network.auth.AuthTokenStorage
import ru.studentai.core.network.auth.EncryptedAuthTokenStorage
import ru.studentai.core.network.client.NetworkJson
import ru.studentai.core.network.client.OkHttpClientFactory
import ru.studentai.core.network.client.RetrofitFactory
import ru.studentai.core.network.interceptor.DisabledNetworkLoggingFlag
import ru.studentai.core.network.interceptor.NetworkLoggingFlag

/**
 * Hilt-биндинги `core_network` для интерфейс→реализация.
 *
 * Важно: `TokenRefresher` ЗДЕСЬ НЕ биндится. Это сознательное решение:
 *  • в `core_network` есть только заглушка [ru.studentai.core.network.auth.NoOpTokenRefresher];
 *  • реальная реализация привязывается из `feature_auth` (там endpoint /auth/refresh);
 *  • если `feature_auth` не подключена — `app`-модуль обязан явно предоставить
 *    `TokenRefresher` (хотя бы NoOp). Это не даст графу Hilt собраться «втихую» без рефреша.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class CoreNetworkBindings {

    @Binds
    @Singleton
    public abstract fun bindAuthTokenStorage(impl: EncryptedAuthTokenStorage): AuthTokenStorage
}

/**
 * Hilt-провайдеры `core_network` — для конкретных экземпляров классов из третьих библиотек
 * ([Json], [OkHttpClient], [Retrofit]) и default-значений flag-абстракций.
 *
 * Все провайдеры — @Singleton. Конкретный `BaseUrlProvider` поставляет app-модуль
 * (так как base URL — это конфигурация приложения, не библиотеки).
 */
@Module
@InstallIn(SingletonComponent::class)
public object CoreNetworkProviders {

    @Provides
    @Singleton
    public fun provideNetworkJson(): Json = NetworkJson.Default

    @Provides
    @Singleton
    public fun provideOkHttpClient(factory: OkHttpClientFactory): OkHttpClient =
        factory.create()

    @Provides
    @Singleton
    public fun provideRetrofit(factory: RetrofitFactory): Retrofit =
        factory.create()

    /**
     * Default — логирование выключено. App-модуль обычно переопределяет binding на
     * `NetworkLoggingFlag { BuildConfig.DEBUG }` через свой @Provides.
     *
     * Поскольку Hilt не поддерживает «default with override» в production, app-уровень
     * не предоставляет свой `@Provides` — он использует этот.
     */
    @Provides
    @Singleton
    public fun provideNetworkLoggingFlag(): NetworkLoggingFlag = DisabledNetworkLoggingFlag()
}
