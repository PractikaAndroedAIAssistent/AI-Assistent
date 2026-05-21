package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.QuickAction

@Immutable
internal data class QuickActionItem(
    val action: QuickAction,
    val labelRes: Int,
    val icon: ImageVector,
)

/**
 * Решётка «быстрых действий» (ТЗ §4.2.2).
 *
 * Скроллится горизонтально, чтобы вмещаться по ширине любого устройства;
 * на больших экранах планшетов отображает несколько строк.
 */
@Composable
internal fun QuickActionsGrid(
    actions: List<QuickActionItem>,
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_quick_actions))
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            items(items = actions, key = { it.action::class.qualifiedName ?: "" }) { item ->
                QuickActionCell(item = item, onClick = { onActionClick(item.action) })
            }
        }
    }
}

@Composable
private fun QuickActionCell(item: QuickActionItem, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(StudentAiTheme.spacing.sm),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(StudentAiTheme.spacing.xs))
            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

internal val StudentQuickActions: List<QuickActionItem> = listOf(
    QuickActionItem(QuickAction.Student.NewNote, R.string.feature_home_quick_action_new_note, StudentAiIcons.Notes),
    QuickActionItem(QuickAction.Student.UploadPdf, R.string.feature_home_quick_action_upload_pdf, StudentAiIcons.Pdf),
    QuickActionItem(QuickAction.Student.OpenAi, R.string.feature_home_quick_action_open_ai, StudentAiIcons.Ai),
    QuickActionItem(QuickAction.Student.NewTest, R.string.feature_home_quick_action_new_test, StudentAiIcons.Test),
    QuickActionItem(QuickAction.Student.Flashcards, R.string.feature_home_quick_action_flashcards, StudentAiIcons.Flashcards),
)

internal val TeacherQuickActions: List<QuickActionItem> = listOf(
    QuickActionItem(QuickAction.Teacher.UploadMaterial, R.string.feature_home_quick_action_upload_material, StudentAiIcons.Upload),
    QuickActionItem(QuickAction.Teacher.CreateTest, R.string.feature_home_quick_action_create_test, StudentAiIcons.Test),
    QuickActionItem(QuickAction.Teacher.OpenAi, R.string.feature_home_quick_action_open_ai, StudentAiIcons.Ai),
    QuickActionItem(QuickAction.Teacher.Analytics, R.string.feature_home_quick_action_analytics, StudentAiIcons.Analytics),
)
