package ru.studentai.feature.flashcards.presentation.study

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.feature.flashcards.domain.algorithm.Sm2Algorithm
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.usecase.GetSetUseCase
import ru.studentai.feature.flashcards.domain.usecase.ObserveCardsInSetUseCase
import ru.studentai.feature.flashcards.domain.usecase.SubmitReviewUseCase
import kotlinx.coroutines.flow.first

@HiltViewModel
public class StudyViewModel @Inject constructor(
    private val getSet: GetSetUseCase,
    private val observeCards: ObserveCardsInSetUseCase,
    private val submitReview: SubmitReviewUseCase,
    private val algorithm: Sm2Algorithm,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<StudyState, StudyEvent, StudyEffect>(
    initialState = StudyState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    /** Очередь карточек, оставшихся в текущей сессии. Локально (не в state) — большая. */
    private var queue: ArrayDeque<Flashcard> = ArrayDeque()

    override fun handleEvent(event: StudyEvent) {
        when (event) {
            is StudyEvent.Init -> initFor(event.setId)
            StudyEvent.FlipClicked -> updateState { it.copy(isFlipped = !it.isFlipped) }
            is StudyEvent.QualitySubmitted -> submitAndAdvance(event)
            StudyEvent.FinishedAcknowledged -> sendEffect(StudyEffect.Closed)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(StudyEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun initFor(setId: String) {
        updateState { it.copy(isLoading = true, setId = setId) }
        launchSafe {
            val set = (getSet(setId) as? DomainResult.Success)?.value
            val name = set?.name.orEmpty()
            val allCards = observeCards(setId).first()
            val due = algorithm.pickDueCards(allCards)
            queue = ArrayDeque(due)
            updateState {
                it.copy(
                    isLoading = false,
                    setName = name,
                    total = due.size,
                    completed = 0,
                    currentCard = queue.removeFirstOrNull(),
                    isFlipped = false,
                )
            }
        }
    }

    private fun submitAndAdvance(event: StudyEvent.QualitySubmitted) {
        val card = currentState.currentCard ?: return
        launchSafe {
            when (val r = submitReview(card.id, event.quality)) {
                is DomainResult.Success -> {
                    val next = queue.removeFirstOrNull()
                    updateState {
                        it.copy(
                            completed = it.completed + 1,
                            currentCard = next,
                            isFlipped = false,
                        )
                    }
                }
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }
}
