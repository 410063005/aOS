package com.example.aos.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactory {
    companion object {
        private const val BASE_URL = "https://api.github.com/"
        private var retrofit: Retrofit? = null

        fun create(): Retrofit {
            if (retrofit == null) {
                val logging = HttpLoggingInterceptor()
                logging.level = if (true) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(logging)
//                    .addInterceptor(headerInterceptor())
//                    .addInterceptor(PageInfoInterceptor())
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
    }
}