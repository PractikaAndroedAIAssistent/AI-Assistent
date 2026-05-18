package ru.studentai.core.common.error

/**
 * Ошибки извлечения текста и OCR-распознавания PDF (ТЗ §4.2.6).
 */
public sealed class PdfException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause) {

    /** Файл повреждён или не является валидным PDF. */
    public class Corrupted(cause: Throwable? = null) :
        PdfException("PDF file is corrupted or unreadable", cause)

    /** PDF зашифрован и не поддерживается. */
    public class Encrypted(cause: Throwable? = null) :
        PdfException("PDF is password-protected", cause)

    /** Не удалось извлечь текстовый слой (требуется OCR). */
    public class NoTextLayer(cause: Throwable? = null) :
        PdfException("PDF has no extractable text layer", cause)

    /** OCR-распознавание не удалось. */
    public class OcrFailed(
        message: String? = null,
        cause: Throwable? = null,
    ) : PdfException(message ?: "OCR recognition failed", cause)

    /** PDF превышает допустимый размер. */
    public class TooLarge(
        public val sizeBytes: Long,
        public val limitBytes: Long,
        cause: Throwable? = null,
    ) : PdfException("PDF size $sizeBytes B exceeds limit $limitBytes B", cause)

    /** Не удалось разбить текст на фрагменты для семантического поиска. */
    public class ChunkingFailed(
        message: String? = null,
        cause: Throwable? = null,
    ) : PdfException(message ?: "Failed to split PDF text into chunks", cause)
}
