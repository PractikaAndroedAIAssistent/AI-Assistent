package ru.studentai.feature.flashcards.presentation.sets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.flashcards.R
import ru.studentai.feature.flashcards.domain.model.FlashcardSet

@Composable
internal fun SetCard(
    set: FlashcardSet,
    onClick: () -> Unit,
    onStudy: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = StudentAiTheme.spacing.md),
        style = AppCardStyle.Outlined,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = set.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                val subject = set.subjectName
                if (!subject.isNullOrBlank()) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(StudentAiTheme.spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DueBadge(dueCount = set.dueCount)
                    Spacer(Modifier.width(StudentAiTheme.spacing.sm))
                    Text(
                        text = stringResource(R.string.feature_flashcards_total_count, set.cardCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = StudentAiIcons.Delete,
                    contentDescription = stringResource(R.string.feature_flashcards_action_delete),
                )
            }
        }
        if (set.cardCount > 0) {
            Spacer(Modifier.height(StudentAiTheme.spacing.sm))
            PrimaryButton(
                text = stringResource(R.string.feature_flashcards_action_study),
                onClick = onStudy,
                modifier = Modifier.fillMaxWidth(),
                enabled = set.dueCount > 0,
                leadingIcon = StudentAiIcons.Play,
            )
        }
    }
}

@Composable
private fun DueBadge(dueCount: Int) {
    val color = if (dueCount > 0) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val onColor = if (dueCount > 0) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(percent = 50))
            .padding(horizontal = StudentAiTheme.spacing.sm, vertical = StudentAiTheme.spacing.xxs),
    ) {
        Text(
            text = stringResource(R.string.feature_flashcards_due_count, dueCount),
            style = MaterialTheme.typography.labelMedium,
            color = onColor,
        )
    }
}
