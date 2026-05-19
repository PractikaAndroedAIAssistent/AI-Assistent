package ru.studentai.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews
import ru.studentai.core.designsystem.theme.StudentAiTheme

/**
 * Полноэкранный индикатор загрузки. Используется как state-холдер при ожидании
 * первичных данных экрана.
 *
 * Для inline-состояний (например, кнопка-loading) используйте [LoadingIndicator].
 */
@Composable
public fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            if (message != null) {
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(top = StudentAiTheme.spacing.md)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Inline-индикатор фиксированного размера — для использования внутри других компонентов.
 */
@Composable
public fun LoadingIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(24.dp),
        strokeWidth = 2.dp,
    )
}

@ThemePreviews
@Composable
private fun LoadingStatePreview() = PreviewBox {
    LoadingState(message = "Загрузка расписания…")
}
