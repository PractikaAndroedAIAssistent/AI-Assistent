package ru.studentai.feature.flashcards.presentation.sets

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.ui.error.ErrorMessageResolver
import ru.studentai.core.ui.mvi.BaseViewModel
import ru.studentai.core.ui.mvi.ContentState
import ru.studentai.feature.auth.domain.usecase.GetProfileUseCase
import ru.studentai.feature.flashcards.domain.usecase.DeleteSetUseCase
import ru.studentai.feature.flashcards.domain.usecase.ObserveSetsUseCase

@HiltViewModel
public class SetsViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val observeSets: ObserveSetsUseCase,
    private val deleteSet: DeleteSetUseCase,
    private val errorResolver: ErrorMessageResolver,
    dispatchers: DispatcherProvider,
    logger: Logger,
) : BaseViewModel<SetsState, SetsEvent, SetsEffect>(
    initialState = SetsState(),
    dispatchers = dispatchers,
    logger = logger,
) {

    private var observeJob: Job? = null
    private var userId: String = ""

    init { load() }

    override fun handleEvent(event: SetsEvent) {
        when (event) {
            SetsEvent.RetryClicked -> load()
            is SetsEvent.SetClicked -> sendEffect(SetsEffect.NavigateToEditSet(event.setId))
            is SetsEvent.StudyClicked -> sendEffect(SetsEffect.NavigateToStudy(event.setId))
            is SetsEvent.DeleteSet -> delete(event.setId)
            SetsEvent.AddSetClicked -> sendEffect(SetsEffect.NavigateToAddSet)
        }
    }

    override fun defaultErrorHandler(error: AppException) {
        sendEffect(SetsEffect.ShowMessage(errorResolver.resolve(error)))
    }

    private fun load() {
        launchSafe {
            when (val profile = getProfile()) {
                is DomainResult.Success -> {
                    userId = profile.value.user.id
                    observeJob?.cancel()
                    updateState { it.copy(sets = ContentState.Loading) }
                    observeJob = observeSets(userId)
                        .onEach { list ->
                            updateState {
                                it.copy(
                                    sets = if (list.isEmpty()) ContentState.Empty
                                    else ContentState.Success(list),
                                )
                            }
                        }
                        .launchIn(viewModelScope)
                }
                is DomainResult.Failure -> defaultErrorHandler(profile.error)
            }
        }
    }

    private fun delete(setId: String) {
        launchSafe {
            when (val r = deleteSet(setId)) {
                is DomainResult.Success -> Unit
                is DomainResult.Failure -> defaultErrorHandler(r.error)
            }
        }
    }
}
