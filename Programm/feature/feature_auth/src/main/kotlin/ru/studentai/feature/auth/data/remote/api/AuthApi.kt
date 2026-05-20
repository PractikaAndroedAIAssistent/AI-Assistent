package ru.studentai.feature.auth.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import ru.studentai.core.network.auth.NoAuth
import ru.studentai.feature.auth.data.remote.dto.AuthResponseDto
import ru.studentai.feature.auth.data.remote.dto.LoginRequestDto
import ru.studentai.feature.auth.data.remote.dto.RefreshRequestDto
import ru.studentai.feature.auth.data.remote.dto.RegisterRequestDto
import ru.studentai.feature.auth.data.remote.dto.TokensDto
import ru.studentai.feature.auth.data.remote.dto.UpdateProfileRequestDto
import ru.studentai.feature.auth.data.remote.dto.UserDto

/**
 * Retrofit-контракт сервера для auth-фичи.
 *
 * Аннотации [NoAuth] помечают endpoint'ы, которые не требуют Bearer-токена —
 * `AuthInterceptor` и `TokenAuthenticator` пропустят их.
 */
public interface AuthApi {

    @NoAuth
    @POST("auth/login")
    public suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @NoAuth
    @POST("auth/register")
    public suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @NoAuth
    @POST("auth/refresh")
    public suspend fun refresh(@Body body: RefreshRequestDto): TokensDto

    @POST("auth/logout")
    public suspend fun logout()

    @GET("users/me")
    public suspend fun currentProfile(): UserDto

    @PUT("users/me")
    public suspend fun updateProfile(@Body body: UpdateProfileRequestDto): UserDto
}
