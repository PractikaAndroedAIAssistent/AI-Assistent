package ru.studentai.core.ui.error

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import ru.studentai.core.common.error.AiException
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.error.NetworkException
import ru.studentai.core.common.error.PdfException
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.error.UnknownException
import ru.studentai.core.common.error.ValidationException
import ru.studentai.core.common.validation.ValidationError
import ru.studentai.core.ui.R
import ru.studentai.core.ui.resource.ResourceProvider

class DefaultErrorMessageResolverTest {

    private val resources: ResourceProvider = mockk<ResourceProvider>(relaxed = true).also { mock ->
        every { mock.getString(R.string.core_ui_error_network_no_connection) } returns "no_connection"
        every { mock.getString(R.string.core_ui_error_network_timeout) } returns "timeout"
        every { mock.getString(R.string.core_ui_error_network_server) } returns "server"
        every { mock.getString(R.string.core_ui_error_network_http, 404) } returns "http_404"
        every { mock.getString(R.string.core_ui_error_network_serialization) } returns "serialization"
        every { mock.getString(R.string.core_ui_error_network_cancelled) } returns "cancelled"

        every { mock.getString(R.string.core_ui_error_auth_invalid_credentials) } returns "invalid_credentials"
        every { mock.getString(R.string.core_ui_error_auth_token_expired) } returns "token_expired"
        every { mock.getString(R.string.core_ui_error_auth_refresh_failed) } returns "refresh_failed"
        every { mock.getString(R.string.core_ui_error_auth_unauthorized) } returns "unauthorized"
        every { mock.getString(R.string.core_ui_error_auth_forbidden) } returns "forbidden"
        every { mock.getString(R.string.core_ui_error_auth_email_taken) } returns "email_taken"
        every { mock.getString(R.string.core_ui_error_auth_account_disabled) } returns "account_disabled"

        every { mock.getString(R.string.core_ui_error_validation_generic) } returns "validation_generic"

        every { mock.getString(R.string.core_ui_error_storage_not_found) } returns "not_found"
        every { mock.getString(R.string.core_ui_error_storage_constraint) } returns "constraint"
        every { mock.getString(R.string.core_ui_error_storage_access_denied) } returns "access_denied"
        every { mock.getString(R.string.core_ui_error_storage_out_of_space) } returns "out_of_space"
        every { mock.getString(R.string.core_ui_error_storage_io) } returns "io"

        every { mock.getString(R.string.core_ui_error_ai_no_data) } returns "ai_no_data"
        every { mock.getString(R.string.core_ui_error_ai_context_limit) } returns "ai_context_limit"
        every { mock.getString(R.string.core_ui_error_ai_rate_limited) } returns "ai_rate_limited"
        every { mock.getString(R.string.core_ui_error_ai_provider) } returns "ai_provider"
        every { mock.getString(R.string.core_ui_error_ai_model_unavailable) } returns "ai_model_unavailable"
        every { mock.getString(R.string.core_ui_error_ai_invalid_response) } returns "ai_invalid_response"

        every { mock.getString(R.string.core_ui_error_pdf_corrupted) } returns "pdf_corrupted"
        every { mock.getString(R.string.core_ui_error_pdf_encrypted) } returns "pdf_encrypted"
        every { mock.getString(R.string.core_ui_error_pdf_no_text_layer) } returns "pdf_no_text_layer"
        every { mock.getString(R.string.core_ui_error_pdf_ocr_failed) } returns "pdf_ocr_failed"
        every { mock.getString(R.string.core_ui_error_pdf_too_large) } returns "pdf_too_large"
        every { mock.getString(R.string.core_ui_error_pdf_chunking_failed) } returns "pdf_chunking_failed"

        every { mock.getString(R.string.core_ui_error_unknown) } returns "unknown"
    }
    private val sut = DefaultErrorMessageResolver(resources)

    @Test
    fun `NoConnection maps to no_connection`() {
        assertThat(sut.resolve(NetworkException.NoConnection())).isEqualTo("no_connection")
    }

    @Test
    fun `Timeout maps to timeout`() {
        assertThat(sut.resolve(NetworkException.Timeout())).isEqualTo("timeout")
    }

    @Test
    fun `Http includes code in formatted string`() {
        assertThat(sut.resolve(NetworkException.Http(code = 404))).isEqualTo("http_404")
    }

    @Test
    fun `Server maps to server`() {
        assertThat(sut.resolve(NetworkException.Server(code = 500))).isEqualTo("server")
    }

    @Test
    fun `Serialization maps to serialization`() {
        assertThat(sut.resolve(NetworkException.Serialization())).isEqualTo("serialization")
    }

    @Test
    fun `Cancelled maps to cancelled`() {
        assertThat(sut.resolve(NetworkException.Cancelled())).isEqualTo("cancelled")
    }

    @Test
    fun `InvalidCredentials maps`() {
        assertThat(sut.resolve(AuthException.InvalidCredentials())).isEqualTo("invalid_credentials")
    }

    @Test
    fun `TokenExpired maps`() {
        assertThat(sut.resolve(AuthException.TokenExpired())).isEqualTo("token_expired")
    }

    @Test
    fun `RefreshFailed maps`() {
        assertThat(sut.resolve(AuthException.RefreshFailed())).isEqualTo("refresh_failed")
    }

    @Test
    fun `Unauthorized maps`() {
        assertThat(sut.resolve(AuthException.Unauthorized())).isEqualTo("unauthorized")
    }

    @Test
    fun `Forbidden maps`() {
        assertThat(sut.resolve(AuthException.Forbidden())).isEqualTo("forbidden")
    }

    @Test
    fun `EmailAlreadyTaken maps`() {
        assertThat(sut.resolve(AuthException.EmailAlreadyTaken())).isEqualTo("email_taken")
    }

    @Test
    fun `AccountDisabled maps`() {
        assertThat(sut.resolve(AuthException.AccountDisabled())).isEqualTo("account_disabled")
    }

    @Test
    fun `Validation uses first error message`() {
        val ex = ValidationException(
            listOf(ValidationError("email", "invalid_format", "Email invalid")),
        )
        assertThat(sut.resolve(ex)).isEqualTo("Email invalid")
    }

    @Test
    fun `Storage NotFound maps`() {
        val ex = StorageException.NotFound(entity = "Note", id = "1")
        assertThat(sut.resolve(ex)).isEqualTo("not_found")
    }

    @Test
    fun `Storage Constraint maps`() {
        assertThat(sut.resolve(StorageException.ConstraintViolation())).isEqualTo("constraint")
    }

    @Test
    fun `Storage AccessDenied maps`() {
        assertThat(sut.resolve(StorageException.AccessDenied())).isEqualTo("access_denied")
    }

    @Test
    fun `Storage OutOfSpace maps`() {
        assertThat(sut.resolve(StorageException.OutOfSpace())).isEqualTo("out_of_space")
    }

    @Test
    fun `Storage Io maps`() {
        assertThat(sut.resolve(StorageException.Io())).isEqualTo("io")
    }

    @Test
    fun `AI NoDataForQuery maps`() {
        assertThat(sut.resolve(AiException.NoDataForQuery(query = "x"))).isEqualTo("ai_no_data")
    }

    @Test
    fun `AI ContextLimitExceeded maps`() {
        assertThat(sut.resolve(AiException.ContextLimitExceeded(tokens = 5000, limit = 4000)))
            .isEqualTo("ai_context_limit")
    }

    @Test
    fun `AI RateLimited maps`() {
        assertThat(sut.resolve(AiException.RateLimited())).isEqualTo("ai_rate_limited")
    }

    @Test
    fun `AI ProviderError maps`() {
        assertThat(sut.resolve(AiException.ProviderError(providerName = "OpenAI"))).isEqualTo("ai_provider")
    }

    @Test
    fun `AI ModelUnavailable maps`() {
        assertThat(sut.resolve(AiException.ModelUnavailable(model = "gpt-4")))
            .isEqualTo("ai_model_unavailable")
    }

    @Test
    fun `AI InvalidResponse maps`() {
        assertThat(sut.resolve(AiException.InvalidResponse())).isEqualTo("ai_invalid_response")
    }

    @Test
    fun `PDF Corrupted maps`() {
        assertThat(sut.resolve(PdfException.Corrupted())).isEqualTo("pdf_corrupted")
    }

    @Test
    fun `PDF Encrypted maps`() {
        assertThat(sut.resolve(PdfException.Encrypted())).isEqualTo("pdf_encrypted")
    }

    @Test
    fun `PDF NoTextLayer maps`() {
        assertThat(sut.resolve(PdfException.NoTextLayer())).isEqualTo("pdf_no_text_layer")
    }

    @Test
    fun `PDF OcrFailed maps`() {
        assertThat(sut.resolve(PdfException.OcrFailed())).isEqualTo("pdf_ocr_failed")
    }

    @Test
    fun `PDF TooLarge maps`() {
        assertThat(sut.resolve(PdfException.TooLarge(sizeBytes = 1024, limitBytes = 512)))
            .isEqualTo("pdf_too_large")
    }

    @Test
    fun `PDF ChunkingFailed maps`() {
        assertThat(sut.resolve(PdfException.ChunkingFailed())).isEqualTo("pdf_chunking_failed")
    }

    @Test
    fun `UnknownException maps to unknown`() {
        assertThat(sut.resolve(UnknownException("any"))).isEqualTo("unknown")
    }
}
