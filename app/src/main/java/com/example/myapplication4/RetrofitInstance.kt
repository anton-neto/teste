package com.example.myapplication4

import NYTimesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val NYTIMES_BASE_URL = "https://api.nytimes.com/svc/topstories/v2/"

interface NYTimesApiService {
    @GET("home.json")
    suspend fun getTopStories(@Query("api-key") apiKey: String): NYTimesResponse
}

object RetrofitInstance {
    val api: NYTimesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NYTIMES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NYTimesApiService::class.java)
    }
}
