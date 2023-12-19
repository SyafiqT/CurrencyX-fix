package com.android.currencyx

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyConverterFragment : Fragment() {

    private lateinit var etFirstConversion: EditText
    private lateinit var etSecondConversion: EditText
    private lateinit var spinnerFirstConversion: Spinner
    private lateinit var spinnerSecondConversion: Spinner

    private var conversionRates: Map<String, Double> = emptyMap()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.currencyapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", "cur_live_ebtkXzTLN6K8qSagb6ttFbp4ZVjeWXKLXHb9yYFs")
                .build()
            chain.proceed(request)
        }.build())
        .build()


    private val currencyApi = retrofit.create(CurrencyApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currency_converter, container, false)

        etFirstConversion = view.findViewById(R.id.et_firstConversion)
        etSecondConversion = view.findViewById(R.id.et_secondConversion)
        spinnerFirstConversion = view.findViewById(R.id.spinner_firstConversion)
        spinnerSecondConversion = view.findViewById(R.id.spinner_secondConversion)

        initializeSpinners()
        initializeEditTextListeners()
        fetchConversionRates()

        return view
    }

    private fun initializeSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_options,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFirstConversion.adapter = adapter
        spinnerSecondConversion.adapter = adapter

        // Set default selections
        spinnerFirstConversion.setSelection(0)
        spinnerSecondConversion.setSelection(1)

        // Spinner item selection listeners
        spinnerFirstConversion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        spinnerSecondConversion.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateConversion()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
    }

    private fun initializeEditTextListeners() {
        etFirstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateConversion()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }
        })
    }

    private fun fetchConversionRates() {
        val call = currencyApi.getConversionRates(
            "IDR",
            apiKey = "cur_live_ebtkXzTLN6K8qSagb6ttFbp4ZVjeWXKLXHb9yYFs"
        ) // Replace "IDR" with your base currency

        call.enqueue(object : Callback<CurrencyApiResponse> {
            override fun onResponse(
                call: Call<CurrencyApiResponse>,
                response: Response<CurrencyApiResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    println("API Response Body: $responseBody")

                    responseBody?.data?.let { data ->
                        conversionRates = data.mapValues { it.value.value }
                        updateConversion()
                    }
                }
            }

            override fun onFailure(call: Call<CurrencyApiResponse>, t: Throwable) {
                // Handle failure (e.g., show an error message)
            }
        })
    }

    private fun updateConversion() {
        val amountStr = etFirstConversion.text.toString()
        if (amountStr.isNotEmpty()) {
            val amount = amountStr.toDouble()
            val fromCurrency = spinnerFirstConversion.selectedItem.toString()
            val toCurrency = spinnerSecondConversion.selectedItem.toString()

            val conversionRate =
                conversionRates[toCurrency]?.div(conversionRates[fromCurrency] ?: 1.0) ?: 0.0
            // Add this before the conversion calculation
            println("From Currency: $fromCurrency, To Currency: $toCurrency, Conversion Rates: $conversionRates")

            val convertedAmount = amount * conversionRate

            etSecondConversion.setText(String.format("%.2f", convertedAmount))
        } else {
            etSecondConversion.text.clear()
        }
    }
}
