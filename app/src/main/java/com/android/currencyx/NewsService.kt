package com.android.currencyx

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("/v2/everything")
    suspend fun getTopHeadlines(
        @Query("q") query: String,
        @Query("from") fromDate: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}

