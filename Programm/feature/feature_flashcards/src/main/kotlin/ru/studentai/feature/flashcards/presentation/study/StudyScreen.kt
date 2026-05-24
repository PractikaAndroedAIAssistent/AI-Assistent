package ru.studentai.feature.flashcards.presentation.study

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.SecondaryButton
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppTopBar
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.feature.flashcards.R
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.ReviewQuality

@Composable
public fun StudyScreen(
    setId: String,
    onClose: () -> Unit,
    viewModel: StudyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(setId) { viewModel.dispatch(StudyEvent.Init(setId)) }
    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            StudyEffect.Closed -> onClose()
            is StudyEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    ScreenScaffold(
        topBar = {
            AppTopBar(
                title = state.setName.ifBlank { stringResource(R.string.feature_flashcards_study_title) },
                onNavigateBack = onClose,
            )
        },
        snackbarHostState = snackbar,
    ) { padding ->
        when {
            state.isLoading -> LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            state.isFinished -> FinishedView(
                modifier = Modifier.fillMaxSize().padding(padding),
                onClose = { viewModel.dispatch(StudyEvent.FinishedAcknowledged) },
            )
            else -> Body(
                state = state,
                onFlip = { viewModel.dispatch(StudyEvent.FlipClicked) },
                onQuality = { viewModel.dispatch(StudyEvent.QualitySubmitted(it)) },
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }
}

@Composable
private fun Body(
    state: StudyState,
    onFlip: () -> Unit,
    onQuality: (ReviewQuality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val card = state.currentCard ?: return
    Column(
        modifier = modifier
            .padding(horizontal = StudentAiTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
        ProgressHeader(state = state)
        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        FlipCard(card = card, isFlipped = state.isFlipped, onClick = onFlip)
        Spacer(Modifier.height(StudentAiTheme.spacing.md))
        if (state.isFlipped) {
            QualityButtons(onQuality = onQuality)
        } else {
            PrimaryButton(
                text = stringResource(R.string.feature_flashcards_action_flip),
                onClick = onFlip,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
    }
}

@Composable
private fun ProgressHeader(state: StudyState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_flashcards_session_progress, state.completed + 1, state.total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.xs))
        LinearProgressIndicator(
            progress = { state.progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FlipCard(
    card: Flashcard,
    isFlipped: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = if (isFlipped) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable(onClick = onClick),
    ) {
        Crossfade(targetState = isFlipped, animationSpec = tween(durationMillis = 250), label = "flip") { flipped ->
            Box(
                modifier = Modifier.fillMaxSize().padding(StudentAiTheme.spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (flipped) card.back else card.front,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun QualityButtons(onQuality: (ReviewQuality) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.xs)) {
        Row(horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.xs), modifier = Modifier.fillMaxWidth()) {
            SecondaryButton(
                text = stringResource(R.string.feature_flashcards_quality_unknown),
                onClick = { onQuality(ReviewQuality.Unknown) },
                modifier = Modifier.weight(1f),
            )
            SecondaryButton(
                text = stringResource(R.string.feature_flashcards_quality_bad),
                onClick = { onQuality(ReviewQuality.Bad) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.xs), modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = stringResource(R.string.feature_flashcards_quality_normal),
                onClick = { onQuality(ReviewQuality.Normal) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = stringResource(R.string.feature_flashcards_quality_good),
                onClick = { onQuality(ReviewQuality.Good) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = stringResource(R.string.feature_flashcards_quality_excellent),
                onClick = { onQuality(ReviewQuality.Excellent) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FinishedView(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    Column(
        modifier = modifier.padding(StudentAiTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.feature_flashcards_session_completed),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(StudentAiTheme.spacing.lg))
        PrimaryButton(
            text = stringResource(R.string.feature_flashcards_action_cancel),
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
