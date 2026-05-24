package ru.studentai.feature.flashcards.presentation.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.studentai.core.designsystem.component.button.PrimaryButton
import ru.studentai.core.designsystem.component.button.SecondaryButton
import ru.studentai.core.designsystem.component.button.TertiaryButton
import ru.studentai.core.designsystem.component.feedback.LoadingState
import ru.studentai.core.designsystem.component.input.AppTextField
import ru.studentai.core.designsystem.component.layout.AppCard
import ru.studentai.core.designsystem.component.layout.AppCardStyle
import ru.studentai.core.designsystem.component.layout.ScreenScaffold
import ru.studentai.core.designsystem.component.navigation.AppTopBar
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.ui.compose.ObserveAsEffects
import ru.studentai.feature.flashcards.R
import ru.studentai.feature.flashcards.domain.model.Flashcard

@Composable
public fun SetEditScreen(
    setId: String?,
    onClose: () -> Unit,
    viewModel: SetEditViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(setId) { viewModel.dispatch(SetEditEvent.Init(setId)) }
    ObserveAsEffects(viewModel.effects) { effect ->
        when (effect) {
            SetEditEffect.Saved, SetEditEffect.Cancelled -> onClose()
            is SetEditEffect.ShowMessage -> scope.launch { snackbar.showSnackbar(effect.message) }
        }
    }

    val titleRes = if (setId == null) R.string.feature_flashcards_set_new
    else R.string.feature_flashcards_set_edit

    ScreenScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(titleRes),
                onNavigateBack = { viewModel.dispatch(SetEditEvent.CancelClicked) },
            )
        },
        snackbarHostState = snackbar,
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            return@ScreenScaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = StudentAiTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm),
        ) {
            item(key = "name") {
                Spacer(Modifier.height(StudentAiTheme.spacing.sm))
                AppTextField(
                    value = state.name,
                    onValueChange = { viewModel.dispatch(SetEditEvent.NameChanged(it)) },
                    label = stringResource(R.string.feature_flashcards_field_set_name),
                    errorMessage = state.nameError,
                )
            }
            item(key = "subject") {
                AppTextField(
                    value = state.subjectName,
                    onValueChange = { viewModel.dispatch(SetEditEvent.SubjectChanged(it)) },
                    label = stringResource(R.string.feature_flashcards_field_subject),
                )
            }
            item(key = "save-buttons") {
                Spacer(Modifier.height(StudentAiTheme.spacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(StudentAiTheme.spacing.sm)) {
                    SecondaryButton(
                        text = stringResource(R.string.feature_flashcards_action_cancel),
                        onClick = { viewModel.dispatch(SetEditEvent.CancelClicked) },
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = stringResource(R.string.feature_flashcards_action_save),
                        onClick = { viewModel.dispatch(SetEditEvent.SaveClicked) },
                        loading = state.isSaving,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (state.setId != null) {
                item(key = "new-card-header") {
                    Spacer(Modifier.height(StudentAiTheme.spacing.md))
                    Text(
                        text = stringResource(R.string.feature_flashcards_action_add_card),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                item(key = "new-card-form") {
                    AppCard(style = AppCardStyle.Outlined) {
                        AppTextField(
                            value = state.newCardFront,
                            onValueChange = { viewModel.dispatch(SetEditEvent.NewCardFrontChanged(it)) },
                            label = stringResource(R.string.feature_flashcards_field_front),
                            singleLine = false,
                            maxLines = 3,
                        )
                        Spacer(Modifier.height(StudentAiTheme.spacing.xs))
                        AppTextField(
                            value = state.newCardBack,
                            onValueChange = { viewModel.dispatch(SetEditEvent.NewCardBackChanged(it)) },
                            label = stringResource(R.string.feature_flashcards_field_back),
                            singleLine = false,
                            maxLines = 4,
                            errorMessage = state.cardError,
                        )
                        Spacer(Modifier.height(StudentAiTheme.spacing.sm))
                        TertiaryButton(
                            text = stringResource(R.string.feature_flashcards_action_add_card),
                            onClick = { viewModel.dispatch(SetEditEvent.AddCardClicked) },
                            leadingIcon = StudentAiIcons.Add,
                        )
                    }
                }
                items(items = state.cards, key = { it.id }) { card ->
                    CardRow(
                        card = card,
                        onDelete = { viewModel.dispatch(SetEditEvent.DeleteCardClicked(card.id)) },
                    )
                }
            }
            item(key = "spacer-end") { Spacer(Modifier.height(StudentAiTheme.spacing.xxl)) }
        }
    }
}

@Composable
private fun CardRow(card: Flashcard, onDelete: () -> Unit) {
    AppCard(style = AppCardStyle.Outlined) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(StudentAiTheme.spacing.xxs))
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = StudentAiIcons.Delete,
                    contentDescription = stringResource(R.string.feature_flashcards_action_delete),
                )
            }
        }
    }
}
