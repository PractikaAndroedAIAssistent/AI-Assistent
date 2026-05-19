package ru.studentai.core.ui.error

import javax.inject.Inject
import javax.inject.Singleton
import ru.studentai.core.common.error.AiException
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.PdfException
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.error.UnknownException
import ru.studentai.core.common.error.ValidationException
import ru.studentai.core.ui.R
import ru.studentai.core.ui.resource.ResourceProvider

/**
 * Дефолтная реализация [ErrorMessageResolver] поверх [ResourceProvider].
 *
 * Маппинг покрывает все sealed-подтипы [AppException] из `core_common`.
 * При появлении нового подтипа сюда придётся добавить ветку — `when` exhaustive
 * (но `AppException` всё-таки не sealed-на-вершине, чтобы можно было расширять,
 * поэтому ветка `else` тоже есть на случай неожиданного типа).
 */
@Singleton
public class DefaultErrorMessageResolver @Inject constructor(
    private val resources: ResourceProvider,
) : ErrorMessageResolver {

    override fun resolve(error: AppException): String = when (error) {
        is NetworkException -> resolveNetwork(error)
        is AuthException -> resolveAuth(error)
        is ValidationException -> resolveValidation(error)
        is StorageException -> resolveStorage(error)
        is AiException -> resolveAi(error)
        is PdfException -> resolvePdf(error)
        is UnknownException -> resources.getString(R.string.core_ui_error_unknown)
    }

    private fun resolveNetwork(error: NetworkException): String = when (error) {
        is NetworkException.NoConnection -> resources.getString(R.string.core_ui_error_network_no_connection)
        is NetworkException.Timeout -> resources.getString(R.string.core_ui_error_network_timeout)
        is NetworkException.Server -> resources.getString(R.string.core_ui_error_network_server)
        is NetworkException.Http -> resources.getString(R.string.core_ui_error_network_http, error.code)
        is NetworkException.Serialization -> resources.getString(R.string.core_ui_error_network_serialization)
        is NetworkException.Cancelled -> resources.getString(R.string.core_ui_error_network_cancelled)
    }

    private fun resolveAuth(error: AuthException): String = when (error) {
        is AuthException.InvalidCredentials -> resources.getString(R.string.core_ui_error_auth_invalid_credentials)
        is AuthException.TokenExpired -> resources.getString(R.string.core_ui_error_auth_token_expired)
        is AuthException.RefreshFailed -> resources.getString(R.string.core_ui_error_auth_refresh_failed)
        is AuthException.Unauthorized -> resources.getString(R.string.core_ui_error_auth_unauthorized)
        is AuthException.Forbidden -> resources.getString(R.string.core_ui_error_auth_forbidden)
        is AuthException.EmailAlreadyTaken -> resources.getString(R.string.core_ui_error_auth_email_taken)
        is AuthException.AccountDisabled -> resources.getString(R.string.core_ui_error_auth_account_disabled)
    }

    private fun resolveValidation(error: ValidationException): String {
        // Если есть message от первой ошибки — показываем её, иначе общий fallback
        return error.errors.firstOrNull()?.message
            ?: resources.getString(R.string.core_ui_error_validation_generic)
    }

    private fun resolveStorage(error: StorageException): String = when (error) {
        is StorageException.NotFound -> resources.getString(R.string.core_ui_error_storage_not_found)
        is StorageException.ConstraintViolation -> resources.getString(R.string.core_ui_error_storage_constraint)
        is StorageException.AccessDenied -> resources.getString(R.string.core_ui_error_storage_access_denied)
        is StorageException.OutOfSpace -> resources.getString(R.string.core_ui_error_storage_out_of_space)
        is StorageException.Io -> resources.getString(R.string.core_ui_error_storage_io)
    }

    private fun resolveAi(error: AiException): String = when (error) {
        is AiException.NoDataForQuery -> resources.getString(R.string.core_ui_error_ai_no_data)
        is AiException.ContextLimitExceeded -> resources.getString(R.string.core_ui_error_ai_context_limit)
        is AiException.RateLimited -> resources.getString(R.string.core_ui_error_ai_rate_limited)
        is AiException.ProviderError -> resources.getString(R.string.core_ui_error_ai_provider)
        is AiException.ModelUnavailable -> resources.getString(R.string.core_ui_error_ai_model_unavailable)
        is AiException.InvalidResponse -> resources.getString(R.string.core_ui_error_ai_invalid_response)
    }

    private fun resolvePdf(error: PdfException): String = when (error) {
        is PdfException.Corrupted -> resources.getString(R.string.core_ui_error_pdf_corrupted)
        is PdfException.Encrypted -> resources.getString(R.string.core_ui_error_pdf_encrypted)
        is PdfException.NoTextLayer -> resources.getString(R.string.core_ui_error_pdf_no_text_layer)
        is PdfException.OcrFailed -> resources.getString(R.string.core_ui_error_pdf_ocr_failed)
        is PdfException.TooLarge -> resources.getString(R.string.core_ui_error_pdf_too_large)
        is PdfException.ChunkingFailed -> resources.getString(R.string.core_ui_error_pdf_chunking_failed)
    }
}
