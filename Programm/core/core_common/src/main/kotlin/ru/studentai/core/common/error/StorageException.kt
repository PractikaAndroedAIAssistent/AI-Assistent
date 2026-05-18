package ru.studentai.core.common.error

/**
 * Ошибки локального и облачного хранилища (Room, DataStore, file system, cloud storage).
 */
public sealed class StorageException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause) {

    /** Запись не найдена по идентификатору. */
    public class NotFound(
        public val entity: String,
        public val id: String,
        cause: Throwable? = null,
    ) : StorageException("$entity with id '$id' not found", cause)

    /** Нарушение ограничения уникальности / целостности. */
    public class ConstraintViolation(
        message: String? = null,
        cause: Throwable? = null,
    ) : StorageException(message ?: "Storage constraint violation", cause)

    /** Нет прав на доступ к файлу/контенту. */
    public class AccessDenied(
        message: String? = null,
        cause: Throwable? = null,
    ) : StorageException(message ?: "Access denied", cause)

    /** Закончилось место на устройстве. */
    public class OutOfSpace(cause: Throwable? = null) :
        StorageException("Out of disk space", cause)

    /** Ошибка чтения/записи (IO). */
    public class Io(
        message: String? = null,
        cause: Throwable? = null,
    ) : StorageException(message ?: "Storage I/O error", cause)
}
