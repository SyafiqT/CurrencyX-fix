package com.android.currencyx

data class Article(val title: String, val urlToImage: String, val source: Source, val publishedAt: String)
data class Source(val name: String)
data class NewsResponse(val articles: List<Article>)

