package com.example.assistentai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Точка входа Hilt-графа.
 *
 * Реализация — Application с аннотацией [HiltAndroidApp]. Никакой логики кроме
 * стартапа Hilt-графа здесь не выполняется (ТЗ §4.1.3 — запуск ≤ 3 секунды).
 *
 * Все стартапы фоновых сервисов (WorkManager, FCM-инициализация, миграции БД)
 * подключаются через App Startup library или Hilt-зависимые компоненты — НЕ через
 * прямые вызовы в [onCreate].
 */
@HiltAndroidApp
public class StudentAiApplication : Application()
