package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppHorizontalDivider
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.PendingReview

/**
 * Карточка «Работы на проверку» (ТЗ §4.2.2 — напоминания о проверке).
 */
@Composable
internal fun PendingReviewsCard(
    reviews: List<PendingReview>,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_pending_reviews))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable || reviews.isEmpty() -> Text(
                    text = stringResource(R.string.feature_home_pending_reviews_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> reviews.forEachIndexed { index, review ->
                    if (index != 0) AppHorizontalDivider(Modifier.padding(vertical = StudentAiTheme.spacing.xs))
                    ReviewRow(review)
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(review: PendingReview) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = review.subject,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
            Text(
                text = "Группа ${review.groupName}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.size(StudentAiTheme.spacing.sm))
        CountBadge(count = review.pendingCount)
    }
}

@Composable
private fun CountBadge(count: Int) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(horizontal = StudentAiTheme.spacing.sm, vertical = StudentAiTheme.spacing.xxs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
