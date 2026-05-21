package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppHorizontalDivider
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.Recommendation

/**
 * Карточка «Рекомендации по подготовке» (ТЗ §4.2.10 + §4.2.7).
 *
 * Источники: feature_grades (на основе оценок), feature_ai (на основе материала).
 */
@Composable
internal fun RecommendationsCard(
    recommendations: List<Recommendation>,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_recommendations))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable || recommendations.isEmpty() -> Text(
                    text = stringResource(R.string.feature_home_recommendations_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> recommendations.forEachIndexed { index, rec ->
                    if (index != 0) {
                        AppHorizontalDivider(Modifier.padding(vertical = StudentAiTheme.spacing.xs))
                    }
                    RecommendationRow(rec)
                }
            }
        }
    }
}

@Composable
private fun RecommendationRow(rec: Recommendation) {
    if (!rec.subject.isNullOrBlank()) {
        Text(
            text = rec.subject,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
    }
    Text(
        text = rec.title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
    Text(
        text = rec.body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
