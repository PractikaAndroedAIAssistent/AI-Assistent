package ru.studentai.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Дополнительные семантические цвета, которых нет в Material 3 token-наборе:
 *  • success / warning / info — для пользовательских уведомлений, статусов задач, badges
 *
 * Доступ из Composable: `StudentAiTheme.semanticColors.success`.
 * Не использовать прямые hex-значения в коде — только через эти токены.
 */
@Immutable
public data class StudentAiSemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,

    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,

    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

public val LightSemanticColors: StudentAiSemanticColors = StudentAiSemanticColors(
    success = Color(0xFF2E7D32),
    onSuccess = Color.White,
    successContainer = Color(0xFFC8E6C9),
    onSuccessContainer = Color(0xFF002106),

    warning = Color(0xFFB28200),
    onWarning = Color.White,
    warningContainer = Color(0xFFFFE082),
    onWarningContainer = Color(0xFF2B1700),

    info = Color(0xFF0277BD),
    onInfo = Color.White,
    infoContainer = Color(0xFFBBDEFB),
    onInfoContainer = Color(0xFF001E2F),
)

public val DarkSemanticColors: StudentAiSemanticColors = StudentAiSemanticColors(
    success = Color(0xFF7BC67E),
    onSuccess = Color(0xFF003913),
    successContainer = Color(0xFF005227),
    onSuccessContainer = Color(0xFFC8E6C9),

    warning = Color(0xFFFFC44C),
    onWarning = Color(0xFF402D00),
    warningContainer = Color(0xFF604000),
    onWarningContainer = Color(0xFFFFE082),

    info = Color(0xFF80D8FF),
    onInfo = Color(0xFF003547),
    infoContainer = Color(0xFF004C66),
    onInfoContainer = Color(0xFFBBDEFB),
)

public val LocalSemanticColors: androidx.compose.runtime.ProvidableCompositionLocal<StudentAiSemanticColors> =
    compositionLocalOf {
        error("StudentAiSemanticColors not provided. Wrap content with StudentAiTheme.")
    }
