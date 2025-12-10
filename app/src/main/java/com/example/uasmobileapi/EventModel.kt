package com.example.uasmobileapi

data class EventResponse(
    val status: Int,
    val message: String,
    val data: List<EventModel>? = null
)

data class SingleEventResponse(
    val status: Int,
    val message: String,
    val data: EventModel? = null
)

data class EventModel(
    val id: String? = null,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String?,
    val status: String
)