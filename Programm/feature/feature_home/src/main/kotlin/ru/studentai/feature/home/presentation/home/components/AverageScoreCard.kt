package ru.studentai.feature.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.R
import ru.studentai.feature.home.domain.model.AverageScoreSummary
import ru.studentai.feature.home.domain.model.ScoreTrend

/**
 * Карточка «Средний балл» (ТЗ §4.2.10).
 *
 * Показывает текущее значение / максимум и тренд (стрелка вверх/вниз/прямая) с цветом
 * по семантике (success/warning/error).
 */
@Composable
internal fun AverageScoreCard(
    summary: AverageScoreSummary?,
    isProviderAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(R.string.feature_home_average_score))
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = StudentAiTheme.spacing.md),
        ) {
            when {
                !isProviderAvailable || summary == null -> Text(
                    text = stringResource(R.string.feature_home_average_score_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> ScoreContent(summary)
            }
        }
    }
}

@Composable
private fun ScoreContent(summary: AverageScoreSummary) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = "%.2f".format(summary.value),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
            Text(
                text = "из %.1f · по %d предметам".format(summary.maxValue, summary.subjectCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TrendBadge(trend = summary.trend)
    }
}

@Composable
private fun TrendBadge(trend: ScoreTrend) {
    val labelRes: Int
    val icon: ImageVector
    val containerColor: Color
    val contentColor: Color
    when (trend) {
        ScoreTrend.Up -> {
            labelRes = R.string.feature_home_score_trend_up
            icon = Icons.AutoMirrored.Filled.TrendingUp
            containerColor = StudentAiTheme.semanticColors.successContainer
            contentColor = StudentAiTheme.semanticColors.onSuccessContainer
        }
        ScoreTrend.Down -> {
            labelRes = R.string.feature_home_score_trend_down
            icon = Icons.AutoMirrored.Filled.TrendingDown
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        }
        ScoreTrend.Flat -> {
            labelRes = R.string.feature_home_score_trend_flat
            icon = Icons.AutoMirrored.Filled.TrendingFlat
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(percent = 50))
            .padding(horizontal = StudentAiTheme.spacing.sm, vertical = StudentAiTheme.spacing.xxs),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(StudentAiTheme.spacing.xs))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}
