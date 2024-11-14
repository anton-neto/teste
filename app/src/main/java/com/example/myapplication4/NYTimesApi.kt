package com.example.myapplication.network

import NYTimesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NYTimesApi {
    @GET("topstories/v2/home.json")
    suspend fun getTopStories(
        @Query("api-key") apiKey: String
    ): NYTimesResponse
}
