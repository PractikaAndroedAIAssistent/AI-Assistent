package ru.studentai.feature.schedule.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.studentai.feature.schedule.data.remote.dto.ImportResponse
import ru.studentai.feature.schedule.data.remote.dto.ScheduleItemDto
import ru.studentai.feature.schedule.data.remote.dto.ScheduleSyncResponse
import ru.studentai.feature.schedule.data.remote.dto.UpsertScheduleItemRequest

/**
 * REST API расписания.
 *
 * Все endpoint'ы требуют Bearer-токен → [ru.studentai.core.network.auth.AuthInterceptor]
 * автоматически добавит заголовок. Аннотация `@NoAuth` НЕ применяется.
 */
public interface ScheduleApi {

    /** Полная синхронизация расписания и предметов пользователя. */
    @GET("schedule/sync")
    public suspend fun sync(): ScheduleSyncResponse

    /** Создать занятие. */
    @POST("schedule/items")
    public suspend fun create(@Body body: UpsertScheduleItemRequest): ScheduleItemDto

    /** Обновить занятие. */
    @PUT("schedule/items/{id}")
    public suspend fun update(
        @Path("id") id: String,
        @Body body: UpsertScheduleItemRequest,
    ): ScheduleItemDto

    /** Удалить занятие. */
    @DELETE("schedule/items/{id}")
    public suspend fun delete(@Path("id") id: String)

    /** Импорт расписания из ЛК вуза (ТЗ §4.2.3). */
    @POST("schedule/import/university")
    public suspend fun importFromUniversity(): ImportResponse
}
