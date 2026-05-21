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
import ru.studentai.feature.home.presentation.home.components.GreetingCard
import ru.studentai.feature.home.presentation.home.components.GroupActivityCard
import ru.studentai.feature.home.presentation.home.components.PendingReviewsCard
import ru.studentai.feature.home.presentation.home.components.QuickActionsGrid
import ru.studentai.feature.home.presentation.home.components.TeacherQuickActions
import ru.studentai.feature.home.presentation.home.components.TeacherTasksCard
import ru.studentai.feature.home.presentation.home.components.UpcomingLessonCard

/**
 * Контент главного экрана для преподавателя (ТЗ §4.2.2):
 *  1. GreetingCard
 *  2. UpcomingLessonCard (ближайшее занятие)
 *  3. TeacherTasksCard
 *  4. QuickActionsGrid (UploadMaterial / CreateTest / OpenAi / Analytics)
 *  5. GroupActivityCard
 *  6. PendingReviewsCard
 */
@Composable
internal fun TeacherHomeContent(
    snapshot: HomeSnapshot.Teacher,
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
        TeacherTasksCard(
            tasks = snapshot.tasks,
            isProviderAvailable = snapshot.isTasksAvailable,
        )
        QuickActionsGrid(
            actions = TeacherQuickActions,
            onActionClick = onQuickAction,
        )
        GroupActivityCard(
            activity = snapshot.groupActivity,
            isProviderAvailable = snapshot.isGroupActivityAvailable,
        )
        PendingReviewsCard(
            reviews = snapshot.pendingReviews,
            isProviderAvailable = snapshot.isPendingReviewsAvailable,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
    }
}
