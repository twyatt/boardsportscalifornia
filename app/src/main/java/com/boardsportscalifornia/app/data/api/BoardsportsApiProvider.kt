package com.boardsportscalifornia.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit

private const val BOARDSPORTS_BASE_URL =
    "https://boardsportscalifornia.com/boardsportscalifornia.com/"

class BoardsportsApiProvider {

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(BODY))
                    .build()
            )
            .baseUrl(BOARDSPORTS_BASE_URL)
            .build()
    }

    val api by lazy {
        retrofit.create(BoardsportsApi::class.java)
    }
}
