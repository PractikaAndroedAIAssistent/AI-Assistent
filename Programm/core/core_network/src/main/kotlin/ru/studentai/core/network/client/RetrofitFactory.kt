package ru.studentai.core.network.client

import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Сборка [Retrofit] из BaseUrlProvider + OkHttpClient + NetworkJson.
 *
 * Создаёт ровно один экземпляр Retrofit на app — все feature_*.api получают proxy через
 * `retrofit.create(MyApi::class.java)`.
 *
 * Если у feature нужен отдельный baseUrl (например, для AI-провайдера) — она создаёт свой
 * Retrofit вручную через эту фабрику, передавая собственный [BaseUrlProvider].
 */
public class RetrofitFactory @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider,
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) {

    public fun create(baseUrl: String = baseUrlProvider.provide()): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
