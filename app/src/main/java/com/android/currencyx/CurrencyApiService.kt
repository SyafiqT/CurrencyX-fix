package com.android.currencyx

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("/v3/latest")
    fun getConversionRates(
        @Query("baseCurrency") baseCurrency: String,
        @Query("apikey") apiKey: String
    ): Call<CurrencyApiResponse>
}


