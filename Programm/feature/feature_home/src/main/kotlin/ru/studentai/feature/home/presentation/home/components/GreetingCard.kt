package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.home.R

/**
 * Карточка приветствия: «Доброе утро/день/вечер/ночи, %ФИО%» + бейдж роли.
 *
 * @param user                текущий пользователь
 * @param currentHour         час дня в местной зоне (тестируемость + dev-overrides)
 */
@Composable
internal fun GreetingCard(
    user: User,
    modifier: Modifier = Modifier,
    currentHour: Int = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour },
) {
    val greetingRes = remember(currentHour) { greetingResForHour(currentHour) }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md),
        style = AppCardStyle.Filled,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(greetingRes) + ",",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            RoleChip(role = user.role)
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RoleChip(role: UserRole) {
    val labelRes = when (role) {
        UserRole.Student -> R.string.feature_home_role_student
        UserRole.Teacher -> R.string.feature_home_role_teacher
    }
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(stringResource(labelRes)) },
        leadingIcon = {
            Icon(
                imageVector = when (role) {
                    UserRole.Student -> StudentAiIcons.Subject
                    UserRole.Teacher -> StudentAiIcons.Materials
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}

private fun greetingResForHour(hour: Int): Int = when (hour) {
    in 5..11 -> R.string.feature_home_greeting_morning
    in 12..17 -> R.string.feature_home_greeting_day
    in 18..22 -> R.string.feature_home_greeting_evening
    else -> R.string.feature_home_greeting_night
}
