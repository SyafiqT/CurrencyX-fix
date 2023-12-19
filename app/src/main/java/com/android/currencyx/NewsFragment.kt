package com.android.currencyx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_news, container, false)

        // Replace with your actual data
        rootView.findViewById<TextView>(R.id.tvTitle).text =
            "Breaking: Global Economy Shows Robust Growth"
        rootView.findViewById<TextView>(R.id.Source).text = "Reuters"
        rootView.findViewById<TextView>(R.id.Date).text = "5 hours ago"

        // Replace with your image loading logic
        Glide.with(this)
            .load(R.drawable.img)
            .centerCrop()
            .into(rootView.findViewById(R.id.image))

        return rootView
    }

    // Inside NewsFragment
    private suspend fun fetchNews() {
        val apiKey = "4495330529db41568733ea44d688ef78"
        val newsService = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/everything?q=tesla&from=2023-11-03&sortBy=publishedAt&apiKey=4495330529db41568733ea44d688ef78")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    newsService.getTopHeadlines("tesla", "2023-11-03", "publishedAt", apiKey)
                val article = response.articles.firstOrNull()

                // Update UI with the fetched data (use withContext to switch to the main thread)
                withContext(Dispatchers.Main) {
                    article?.let {
                        rootView.findViewById<TextView>(R.id.tvTitle).text = it.title
                        rootView.findViewById<TextView>(R.id.Source).text = it.source.name
                        rootView.findViewById<TextView>(R.id.Date).text = it.publishedAt

                        // Use a library like Glide to load the image
                        Glide.with(this@NewsFragment)
                            .load(it.urlToImage)
                            .centerCrop()
                            .into(rootView.findViewById(R.id.image))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle errors
            }
        }

    }
}
