package ru.studentai.core.network.auth

/**
 * Маркер для Retrofit-endpoint'ов, которые не требуют Authorization-заголовка.
 *
 * Применяется к публичным endpoint'ам: login / register / refresh / password reset / status.
 *
 * ```
 * interface AuthApi {
 *     @NoAuth
 *     @POST("auth/login")
 *     suspend fun login(@Body body: LoginRequest): LoginResponse
 *
 *     @NoAuth
 *     @POST("auth/refresh")
 *     suspend fun refresh(@Body body: RefreshRequest): TokensResponse
 * }
 * ```
 *
 * [AuthInterceptor] и [TokenAuthenticator] определяют наличие аннотации через
 * `Invocation.method().isAnnotationPresent(NoAuth::class.java)` и в этом случае
 * не пытаются прикреплять или обновлять токен.
 *
 * RUNTIME retention — иначе аннотация будет недоступна в reflection.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
public annotation class NoAuth
