package ru.studentai.feature.tasks.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.studentai.feature.tasks.data.remote.dto.TaskDto
import ru.studentai.feature.tasks.data.remote.dto.TaskListResponse
import ru.studentai.feature.tasks.data.remote.dto.UpsertTaskRequest

public interface TasksApi {

    @GET("tasks")
    public suspend fun list(@Query("role") role: String): TaskListResponse

    @POST("tasks")
    public suspend fun create(@Body body: UpsertTaskRequest): TaskDto

    @PUT("tasks/{id}")
    public suspend fun update(@Path("id") id: String, @Body body: UpsertTaskRequest): TaskDto

    @POST("tasks/{id}/toggle")
    public suspend fun toggle(@Path("id") id: String): TaskDto

    @DELETE("tasks/{id}")
    public suspend fun delete(@Path("id") id: String)
}
