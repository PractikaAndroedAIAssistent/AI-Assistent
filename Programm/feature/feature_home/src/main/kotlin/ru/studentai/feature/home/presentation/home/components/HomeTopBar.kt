package ru.studentai.feature.home.presentation.home.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.studentai.core.designsystem.component.navigation.AppCenterAlignedTopBar
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.feature.home.R

/**
 * Верхняя панель главного экрана.
 *
 * Заголовок «Главная», справа — Profile-кнопка (ТЗ §4.2.2: переход на профиль из главной).
 */
@Composable
internal fun HomeTopBar(
    onProfileClick: () -> Unit,
) {
    AppCenterAlignedTopBar(
        title = stringResource(R.string.feature_home_title),
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = StudentAiIcons.Profile,
                    contentDescription = stringResource(R.string.feature_home_action_profile),
                )
            }
        },
    )
}
