package ru.studentai.feature.flashcards.presentation.sets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.studentai.core.designsystem.component.feedback.EmptyState
import ru.studentai.core.designsystem.component.feedback.ErrorState
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppCenterAlignedTopBar
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.flashcards.R
import ru.studentai.feature.flashcards.presentation.sets.components.SetCard

@Composable
public fun SetsScreen(
    onNavigateToAddSet: () -> Unit,
    onNavigateToEditSet: (String) -> Unit,
    onNavigateToStudy: (String) -> Unit,
    viewModel: SetsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            SetsEffect.NavigateToAddSet -> onNavigateToAddSet()
            is SetsEffect.NavigateToEditSet -> onNavigateToEditSet(effect.setId)
            is SetsEffect.NavigateToStudy -> onNavigateToStudy(effect.setId)
            is SetsEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    ScreenScaffold(
        topBar = {
            AppCenterAlignedTopBar(title = stringResource(R.string.feature_flashcards_title))
        },
        snackbarHostState = snackbar,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.dispatch(SetsEvent.AddSetClicked) },
                icon = { Icon(StudentAiIcons.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.feature_flashcards_action_add_set)) },
            )
        },
    ) { padding ->
        when (val s = state.sets) {
            ContentState.Idle, ContentState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            ContentState.Empty -> EmptyState(
                title = stringResource(R.string.feature_flashcards_empty),
                icon = StudentAiIcons.Flashcards,
                actionLabel = stringResource(R.string.feature_flashcards_empty_action_add),
                onAction = { viewModel.dispatch(SetsEvent.AddSetClicked) },
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is ContentState.Error -> ErrorState(
                title = stringResource(R.string.feature_flashcards_title),
                message = s.error.message ?: "—",
                onRetry = { viewModel.dispatch(SetsEvent.RetryClicked) },
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is ContentState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = StudentAiTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
            ) {
                items(items = s.data, key = { it.id }) { set ->
                    SetCard(
                        set = set,
                        onClick = { viewModel.dispatch(SetsEvent.SetClicked(set.id)) },
                        onStudy = { viewModel.dispatch(SetsEvent.StudyClicked(set.id)) },
                        onDelete = { viewModel.dispatch(SetsEvent.DeleteSet(set.id)) },
                    )
                }
            }
        }
    }
}
