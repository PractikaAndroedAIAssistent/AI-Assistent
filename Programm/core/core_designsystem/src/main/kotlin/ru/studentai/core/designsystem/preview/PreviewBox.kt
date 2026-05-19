package ru.studentai.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Стандартная обёртка для `@Preview`-функций в feature-модулях.
 * Применяет тему и фон, чтобы превью отображались согласованно.
 *
 * Использование:
 * ```
 * @Preview
 * @Composable
 * private fun MyButtonPreview() = PreviewBox {
 *     PrimaryButton(text = "Hello", onClick = {})
 * }
 * ```
 */
@Composable
public fun PreviewBox(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    StudentAiTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(StudentAiTheme.spacing.md),
            ) {
                content()
            }
        }
    }
}

/**
 * Multi-preview annotation. Создаёт два превью — для light и dark темы —
 * за одно объявление, экономя десятки повторных функций.
 *
 * ```
 * @ThemePreviews
 * @Composable
 * private fun MyButtonPreviews() = PreviewBox { PrimaryButton(...) }
 * ```
 */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
public annotation class ThemePreviews
