package com.android.currencyx

data class CurrencyApiResponse(val data: Map<String, CurrencyData>)
data class CurrencyData(val code: String, val value: Double)


