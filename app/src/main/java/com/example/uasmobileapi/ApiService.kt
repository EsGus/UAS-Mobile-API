package com.example.uasmobileapi

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api.php")
    suspend fun getAllEvents(): Response<EventResponse>

    @POST("api.php")
    suspend fun createEvent(@Body event: EventModel): Response<SingleEventResponse>

    @PUT("api.php")
    suspend fun updateEvent(
        @Query("id") id: String,
        @Body event: EventModel
    ): Response<SingleEventResponse>

    @DELETE("api.php")
    suspend fun deleteEvent(@Query("id") id: String): Response<SingleEventResponse>
}