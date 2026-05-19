package ru.studentai.core.network.client

/**
 * Источник base URL для Retrofit. Вынесен в отдельный fun interface, чтобы:
 *  • в release-сборке провайдер возвращал production URL;
 *  • в debug-сборке мог брать значение из настроек разработчика (DataStore);
 *  • в тестах легко подменялся на адрес MockWebServer.
 *
 * Реализация по умолчанию инжектится в app-модуле через @Provides
 * (см. документацию к [CoreNetworkProviders]).
 *
 * Контракт:
 *  • возвращает строку с trailing slash (Retrofit требование);
 *  • всегда HTTPS в release-сборке (см. ТЗ §4.1.5).
 */
public fun interface BaseUrlProvider {
    public fun provide(): String
}

/** Простая статическая реализация — для случаев без динамического переключения. */
public class StaticBaseUrlProvider(private val url: String) : BaseUrlProvider {
    init {
        require(url.endsWith("/")) { "Base URL must end with '/', got: $url" }
    }
    override fun provide(): String = url
}
