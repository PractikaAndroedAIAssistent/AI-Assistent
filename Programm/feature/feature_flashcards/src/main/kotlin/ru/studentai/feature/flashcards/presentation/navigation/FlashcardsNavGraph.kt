package ru.studentai.feature.flashcards.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.studentai.feature.flashcards.presentation.edit.SetEditScreen
import ru.studentai.feature.flashcards.presentation.sets.SetsScreen
import ru.studentai.feature.flashcards.presentation.study.StudyScreen

public fun NavGraphBuilder.flashcardsGraph(
    onNavigateToAddSet: () -> Unit,
    onNavigateToEditSet: (String) -> Unit,
    onNavigateToStudy: (String) -> Unit,
    onCloseEditor: () -> Unit,
    onCloseStudy: () -> Unit,
) {
    composable<FlashcardsRoutes.Sets> {
        SetsScreen(
            onNavigateToAddSet = onNavigateToAddSet,
            onNavigateToEditSet = onNavigateToEditSet,
            onNavigateToStudy = onNavigateToStudy,
        )
    }
    composable<FlashcardsRoutes.SetEdit> { entry ->
        val args = entry.toRoute<FlashcardsRoutes.SetEdit>()
        SetEditScreen(setId = args.setId, onClose = onCloseEditor)
    }
    composable<FlashcardsRoutes.Study> { entry ->
        val args = entry.toRoute<FlashcardsRoutes.Study>()
        StudyScreen(setId = args.setId, onClose = onCloseStudy)
    }
}
