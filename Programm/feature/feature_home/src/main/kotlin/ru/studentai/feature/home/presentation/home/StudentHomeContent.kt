package ru.studentai.feature.home.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.model.QuickAction
import ru.studentai.feature.home.presentation.home.components.AverageScoreCard
import ru.studentai.feature.home.presentation.home.components.GreetingCard
import ru.studentai.feature.home.presentation.home.components.QuickActionsGrid
import ru.studentai.feature.home.presentation.home.components.RecommendationsCard
import ru.studentai.feature.home.presentation.home.components.StudentQuickActions
import ru.studentai.feature.home.presentation.home.components.UpcomingLessonCard
import ru.studentai.feature.home.presentation.home.components.WeekDeadlinesCard

/**
 * Контент главного экрана для студента (ТЗ §4.2.2).
 *
 * Композиция:
 *  1. GreetingCard
 *  2. UpcomingLessonCard
 *  3. WeekDeadlinesCard
 *  4. QuickActionsGrid
 *  5. AverageScoreCard
 *  6. RecommendationsCard
 */
@Composable
internal fun StudentHomeContent(
    snapshot: HomeSnapshot.Student,
    contentPadding: PaddingValues,
    onQuickAction: (QuickAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        GreetingCard(user = snapshot.user)

        UpcomingLessonCard(
            lesson = snapshot.upcomingLesson,
            isProviderAvailable = snapshot.isScheduleAvailable,
        )
        WeekDeadlinesCard(
            deadlines = snapshot.weekDeadlines,
            isProviderAvailable = snapshot.isDeadlinesAvailable,
        )
        QuickActionsGrid(
            actions = StudentQuickActions,
            onActionClick = onQuickAction,
        )
        AverageScoreCard(
            summary = snapshot.averageScore,
            isProviderAvailable = snapshot.isGradesAvailable,
        )
        RecommendationsCard(
            recommendations = snapshot.recommendations,
            isProviderAvailable = snapshot.isRecommendationsAvailable,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
    }
}
