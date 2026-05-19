package ru.studentai.core.network.client

import kotlinx.serialization.json.Json

/**
 * Единая конфигурация kotlinx-serialization для всего сетевого слоя.
 *
 * Решения и обоснования:
 *  • `ignoreUnknownKeys = true` — сервер может добавлять поля, клиент не должен падать;
 *  • `isLenient = true` — допускаем нестрогие значения (например, числа в строках);
 *  • `encodeDefaults = false` — не отправлять default-значения, экономия трафика;
 *  • `explicitNulls = false` — null-поля не сериализуются (короче payload);
 *  • `coerceInputValues = true` — несоответствующие enum/null приводятся к default.
 *
 * Этот же `Json` используется внутри feature-модулей для сериализации DTO.
 */
public object NetworkJson {

    public val Default: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        explicitNulls = false
        coerceInputValues = true
        prettyPrint = false
        useArrayPolymorphism = false
    }
}
