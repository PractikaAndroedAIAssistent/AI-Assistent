package ru.studentai.feature.auth.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.create
import ru.studentai.core.network.auth.TokenRefresher
import ru.studentai.feature.auth.data.remote.api.AuthApi
import ru.studentai.feature.auth.data.repository.AuthTokenRefresherImpl

/**
 * Hilt-биндинги data-слоя feature_auth.
 *
 * Ключевое: здесь регистрируется реализация [TokenRefresher] —
 * это закрывает контракт `core_network`, без которой Hilt-граф приложения не соберётся.
 *
 * Выбор реализации [ru.studentai.feature.auth.domain.repository.AuthRepository]
 * сделан вне этой фичи: app-модуль явно биндит её на одну из реализаций
 * ([ru.studentai.feature.auth.data.repository.AuthRepositoryImpl] для production
 * или demo-реализацию в dev-среде). Это позволяет менять backend-конфигурацию
 * без правок самой фичи.
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class AuthDataBindings {

    @Binds
    @Singleton
    public abstract fun bindTokenRefresher(impl: AuthTokenRefresherImpl): TokenRefresher
}

@Module
@InstallIn(SingletonComponent::class)
public object AuthDataProviders {

    @Provides
    @Singleton
    public fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()
}
