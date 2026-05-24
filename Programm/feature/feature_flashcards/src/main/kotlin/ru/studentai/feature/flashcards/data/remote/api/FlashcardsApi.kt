package ru.studentai.feature.flashcards.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.studentai.feature.flashcards.data.remote.dto.FlashcardDto
import ru.studentai.feature.flashcards.data.remote.dto.FlashcardSetDto
import ru.studentai.feature.flashcards.data.remote.dto.FlashcardSyncResponse
import ru.studentai.feature.flashcards.data.remote.dto.ReviewSubmitRequest
import ru.studentai.feature.flashcards.data.remote.dto.UpsertCardRequest
import ru.studentai.feature.flashcards.data.remote.dto.UpsertSetRequest

public interface FlashcardsApi {

    @GET("flashcards/sync")
    public suspend fun sync(): FlashcardSyncResponse

    @POST("flashcards/sets")
    public suspend fun createSet(@Body body: UpsertSetRequest): FlashcardSetDto

    @PUT("flashcards/sets/{id}")
    public suspend fun updateSet(
        @Path("id") id: String,
        @Body body: UpsertSetRequest,
    ): FlashcardSetDto

    @DELETE("flashcards/sets/{id}")
    public suspend fun deleteSet(@Path("id") id: String)

    @POST("flashcards/cards")
    public suspend fun createCard(@Body body: UpsertCardRequest): FlashcardDto

    @PUT("flashcards/cards/{id}")
    public suspend fun updateCard(
        @Path("id") id: String,
        @Body body: UpsertCardRequest,
    ): FlashcardDto

    @DELETE("flashcards/cards/{id}")
    public suspend fun deleteCard(@Path("id") id: String)

    @POST("flashcards/cards/{id}/review")
    public suspend fun submitReview(
        @Path("id") id: String,
        @Body body: ReviewSubmitRequest,
    ): FlashcardDto
}
