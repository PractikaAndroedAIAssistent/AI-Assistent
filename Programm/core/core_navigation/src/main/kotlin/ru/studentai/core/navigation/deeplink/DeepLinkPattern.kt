package ru.studentai.core.navigation.deeplink

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink

/**
 * Helper для генерации Deep links к навигационным routes.
 *
 * Конвенция StudentAI: scheme `studentai`, host = название фичи.
 *
 * Пример:
 * ```
 * // PDF чат:  studentai://ai/chat/{pdfId}
 * composable<AiChatRoute>(
 *     deepLinks = DeepLinkPattern.studentAi("ai/chat/{pdfId}"),
 * ) { ... }
 * ```
 *
 * Для FCM push-ссылок ТЗ §4.2.11: используется тот же scheme.
 */
public object DeepLinkPattern {

    public const val SCHEME: String = "studentai"

    /** Создаёт список из одного deep link с указанным path-template. */
    public fun studentAi(pathTemplate: String): List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$SCHEME://$pathTemplate" },
    )

    /** Произвольный scheme — для случаев интеграции с web-доменом университета. */
    public fun custom(scheme: String, pathTemplate: String): List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "$scheme://$pathTemplate" },
    )
}
