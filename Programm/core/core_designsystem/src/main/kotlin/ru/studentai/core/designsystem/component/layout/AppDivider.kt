package ru.studentai.core.designsystem.component.layout

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Тонкий горизонтальный разделитель в стиле M3 (1 dp, outlineVariant).
 */
@Composable
public fun AppHorizontalDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

/**
 * Вертикальный разделитель — используется в Row-листингах.
 */
@Composable
public fun AppVerticalDivider(
    modifier: Modifier = Modifier,
) {
    VerticalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
