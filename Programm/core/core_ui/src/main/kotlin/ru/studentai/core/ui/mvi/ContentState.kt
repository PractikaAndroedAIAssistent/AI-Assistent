package ru.studentai.core.ui.mvi

import androidx.compose.runtime.Immutable
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.result.DomainResult

/**
 * Универсальное состояние «контента» экрана — четырёхзначная sealed-обёртка.
 *
 * Использовать как поле `UiState` для блоков, которые загружаются асинхронно:
 *
 * ```
 * data class ScheduleUiState(
 *     val content: ContentState<List<Lesson>> = ContentState.Idle,
 *     val filter: ScheduleFilter = ScheduleFilter.Day,
 * ) : UiState
 * ```
 *
 * `ContentState` отделяет «нет состояния ещё», «грузится», «успех с данными»,
 * «пусто» и «ошибка» — что делает рендер декларативным:
 *
 * ```
 * when (val s = state.content) {
 *     ContentState.Idle, ContentState.Loading -> LoadingState()
 *     is ContentState.Success -> Schedule(s.data)
 *     ContentState.Empty -> EmptyState(...)
 *     is ContentState.Error -> ErrorState(...)
 * }
 * ```
 */
@Immutable
public sealed interface ContentState<out T> {

    /** Начальное состояние до первой попытки загрузки. */
    public data object Idle : ContentState<Nothing>

    /** Идёт загрузка / refresh. */
    public data object Loading : ContentState<Nothing>

    /** Данные успешно получены. */
    public data class Success<T>(val data: T) : ContentState<T>

    /** Запрос успешен, но коллекция пуста / нет результата. */
    public data object Empty : ContentState<Nothing>

    /** Ошибка загрузки — UI должен показать [ru.studentai.core.designsystem.component.feedback.ErrorState]. */
    public data class Error(val error: AppException) : ContentState<Nothing>

    public companion object {
        /** Удобный конструктор из [DomainResult] + предикат «пусто». */
        public inline fun <T> from(
            result: DomainResult<T>,
            isEmpty: (T) -> Boolean = { false },
        ): ContentState<T> = when (result) {
            is DomainResult.Success -> if (isEmpty(result.value)) Empty else Success(result.value)
            is DomainResult.Failure -> Error(result.error)
        }
    }
}

/** `true`, если состояние — loading-промежуточное (или начальное). */
public val ContentState<*>.isLoadingOrIdle: Boolean
    get() = this is ContentState.Idle || this is ContentState.Loading

/** Возвращает данные, если Success, иначе `null`. */
public fun <T> ContentState<T>.dataOrNull(): T? =
    (this as? ContentState.Success<T>)?.data
