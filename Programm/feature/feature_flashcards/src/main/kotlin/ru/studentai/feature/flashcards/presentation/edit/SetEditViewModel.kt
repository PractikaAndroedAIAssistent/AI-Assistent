package ru.studentai.feature.flashcards.presentation.edit

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.flashcards.data.repository.FlashcardRepositoryImpl
import ru.studentai.feature.flashcards.domain.model.Flashcard
import ru.studentai.feature.flashcards.domain.model.FlashcardSet
import ru.studentai.feature.flashcards.domain.usecase.DeleteCardUseCase
import ru.studentai.feature.flashcards.domain.usecase.GetSetUseCase
import ru.studentai.feature.flashcards.domain.usecase.ObserveCardsInSetUseCase
import ru.studentai.feature.flashcards.domain.usecase.UpsertCardUseCase
import ru.studentai.feature.flashcards.domain.usecase.UpsertSetUseCase

@HiltViewModel
public class SetEditViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val getSet: GetSetUseCase,
    private val observeCards: ObserveCardsInSetUseCase,
    private val upsertSet: UpsertSetUseCase,
    private val upsertCard: UpsertCardUseCase,
    private val deleteCard: DeleteCardUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<SetEditState, SetEditEvent, SetEditEffect>(
    initialState = SetEditState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var userId: String = ""
    private var observeJob: Job? = null

    override fun handleEvent(event: SetEditEvent) {
        when (event) {
            is SetEditEvent.Init -> initFor(event.setId)
            is SetEditEvent.NameChanged -> updateState { it.copy(name = event.value) }
            is SetEditEvent.SubjectChanged -> updateState { it.copy(subjectName = event.value) }
            is SetEditEvent.NewCardFrontChanged -> updateState { it.copy(newCardFront = event.value) }
            is SetEditEvent.NewCardBackChanged -> updateState { it.copy(newCardBack = event.value) }
            SetEditEvent.AddCardClicked -> addCard()
            is SetEditEvent.DeleteCardClicked -> deleteCardNow(event.cardId)
            SetEditEvent.SaveClicked -> save()
            SetEditEvent.CancelClicked -> sendEffect(SetEditEffect.Cancelled)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(SetEditEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun initFor(setId: String?) {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> userId = profile.value.user.id
                is DomainResult.Failure -> {
                    defaultErrorHandler(profile.error)
                    return@launchSafe
                }
            }
            if (setId == null) {
                updateState { it.copy(setId = null) }
                return@launchSafe
            }
            updateState { it.copy(isLoading = true, setId = setId) }
            when (val r = getSet(setId)) {
                is DomainResult.Success -> {
                    val set = r.value
                    updateState {
                        it.copy(
                            isLoading = false,
                            name = set.name,
                            subjectName = set.subjectName.orEmpty(),
                        )
                    }
                    observeJob?.cancel()
                    observeJob = observeCards(setId)
                        .onEach { list -> updateState { it.copy(cards = list) } }
                        .launchIn(viewModelScope)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isLoading = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    private fun addCard() {
        val state = currentState
        if (state.setId == null) {
            updateState { it.copy(cardError = "Сначала сохраните набор") }
            return
        }
        if (state.newCardFront.isBlank() || state.newCardBack.isBlank()) {
            updateState { it.copy(cardError = "Введите вопрос и ответ") }
            return
        }
        val now = Clock.System.now()
        val card = Flashcard(
            id = "",
            setId = state.setId,
            ownerUserId = userId,
            front = state.newCardFront.trim(),
            back = state.newCardBack.trim(),
            createdAt = now,
        )
        launchSafe {
            when (val r = upsertCard(card)) {
                is DomainResult.Success -> {
                    updateState {
                        it.copy(
                            newCardFront = "",
                            newCardBack = "",
                            cardError = null,
                        )
                    }
                }
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }

    private fun deleteCardNow(cardId: String) {
        launchSafe {
            when (val r = deleteCard(cardId)) {
                is DomainResult.Success -> Unit
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }

    private fun save() {
        val state = currentState
        if (state.name.isBlank()) {
            updateState { it.copy(nameError = "Введите название набора") }
            return
        }
        updateState { it.copy(isSaving = true, nameError = null) }
        launchSafe {
            val now = Clock.System.now()
            val set = FlashcardSet(
                id = state.setId.orEmpty(),
                ownerUserId = userId,
                name = state.name.trim(),
                subjectName = state.subjectName.trim().ifBlank { null },
                cardCount = state.cards.size,
                dueCount = 0,
                createdAt = now,
                updatedAt = now,
            )
            when (val r = upsertSet(set)) {
                is DomainResult.Success -> {
                    updateState { it.copy(isSaving = false) }
                    sendEffect(SetEditEffect.Saved)
                }
                is DomainResult.Failure -> {
                    updateState { it.copy(isSaving = false) }
                    defaultErrorHandler(r.error)
                }
            }
        }
    }

    @Suppress("unused")
    private fun unusedKeepImport(): String = FlashcardRepositoryImpl.generateId()
}
