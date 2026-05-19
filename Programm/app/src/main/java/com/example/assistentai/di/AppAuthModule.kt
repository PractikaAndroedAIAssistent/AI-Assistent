package com.example.assistentai.di

import com.example.assistentai.auth.DemoAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.studentai.feature.auth.domain.repository.AuthRepository

/**
 * App-уровневый выбор реализации [AuthRepository].
 *
 * Сейчас приложение запускается без реального backend — биндится
 * [DemoAuthRepository] (in-memory) чтобы можно было пройти flow Login → Welcome → Logout.
 *
 * При появлении сервера здесь меняется одна строка:
 * ```
 * @Binds @Singleton
 * abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
public abstract class AppAuthModule {

    @Binds
    @Singleton
    public abstract fun bindAuthRepository(impl: DemoAuthRepository): AuthRepository
}
